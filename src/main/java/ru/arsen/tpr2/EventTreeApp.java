package ru.arsen.tpr2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EventTreeApp extends Application {
    private TreeView<EventNode> treeView;
    private List<EventNode> events = new ArrayList<>();
    private TextField tbRootName;
    private ComboBox<String> cbRootType;
    private TextArea tbFAL, tbCalc;
    private Label lblProb, lblOcenka;
    private Spinner<Double> nudCost;
    private FileChooser fileChooser = new FileChooser();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        treeView = new TreeView<>();
        tbRootName = new TextField("Root Event");
        cbRootType = new ComboBox<>();
        cbRootType.getItems().addAll("AND", "OR");
        cbRootType.getSelectionModel().select(0);

        Button btnAdd = new Button("Add Node");
        btnAdd.setOnAction(e -> addNode());

        Button btnRemove = new Button("Remove Node");
        btnRemove.setOnAction(e -> removeNode());

        Button btnCalc = new Button("Calculate");
        btnCalc.setOnAction(e -> calculateRisk());

        Button btnImport = new Button("Import");
        btnImport.setOnAction(e -> importData());

        Button btnExport = new Button("Export");
        btnExport.setOnAction(e -> exportData());

        tbFAL = new TextArea();
        tbCalc = new TextArea();
        lblProb = new Label("Probability: ");
        lblOcenka = new Label("Risk Evaluation: ");
        nudCost = new Spinner<>(0.0, 1000000.0, 100.0, 10.0);

        VBox layout = new VBox(10, treeView, tbRootName, cbRootType, btnAdd, btnRemove, btnCalc, btnImport, btnExport, tbFAL, tbCalc, lblProb, lblOcenka, nudCost);
        Scene scene = new Scene(layout, 600, 700);

        initRoot();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Event Tree");
        primaryStage.show();
    }

    private void initRoot() {
        EventNode rootEvent = new EventNode("Root Event", EventType.AND, null);
        events.add(rootEvent);
        TreeItem<EventNode> rootItem = new TreeItem<>(rootEvent);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);
    }

    private void addNode() {
        TreeItem<EventNode> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        EventNode newNode = new EventNode("New Event", EventType.OR, selected.getValue());
        events.add(newNode);

        TreeItem<EventNode> newItem = new TreeItem<>(newNode);
        selected.getChildren().add(newItem);
        selected.setExpanded(true);
    }

    private void removeNode() {
        TreeItem<EventNode> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected == treeView.getRoot()) return;

        EventNode nodeToRemove = selected.getValue();
        events.remove(nodeToRemove);
        selected.getParent().getChildren().remove(selected);
    }

    private void calculateRisk() {
        lblProb.setText("Probability: Calculated");
        lblOcenka.setText("Risk Evaluation: Calculated");
    }

    private void importData() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                events.clear();
                nudCost.getValueFactory().setValue(Double.parseDouble(reader.readLine()));
                int count = Integer.parseInt(reader.readLine());

                for (int i = 0; i < count; i++) {
                    String typeText = reader.readLine();
                    String name = reader.readLine();
                    int id = Integer.parseInt(reader.readLine());
                    String parentText = reader.readLine();
                    double probably = Double.parseDouble(reader.readLine());

                    EventType type = typeText.equals("AND") ? EventType.AND : EventType.OR;
                    EventNode node = new EventNode(name, type, null);
                    node.id = id;
                    node.probability = probably;
                    events.add(node);
                }
                treeView.setRoot(new TreeItem<>(events.get(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportData() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(nudCost.getValue().toString());
                writer.newLine();
                writer.write(String.valueOf(events.size()));
                writer.newLine();

                for (EventNode node : events) {
                    writer.write(node.type.name());
                    writer.newLine();
                    writer.write(node.name);
                    writer.newLine();
                    writer.write(String.valueOf(node.id));
                    writer.newLine();
                    writer.write(node.parent != null ? String.valueOf(node.parent.id) : "");
                    writer.newLine();
                    writer.write(String.valueOf(node.probability));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private enum EventType { AND, OR, INIT }

    private static class EventNode {
        String name;
        EventType type;
        EventNode parent;
        int id;
        double probability;

        public EventNode(String name, EventType type, EventNode parent) {
            this.name = name;
            this.type = type;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return (type == EventType.AND ? "[AND] " : "[OR] ") + name;
        }
    }
}
