package ru.arsen.tpr2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private TreeView<EventNode> treeView;
    private List<EventNode> events = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        treeView = new TreeView<>();
        Button addButton = new Button("Добавить событие");
        Button editButton = new Button("Редактировать");
        Button deleteButton = new Button("Удалить");
        Button calculateButton = new Button("Рассчитать риск");
        Label probabilityLabel = new Label("Вероятность риска: 0.0");

        // Корневой узел
        EventNode rootEvent = new EventNode("Корневое событие", EventType.AND);
        events.add(rootEvent);
        TreeItem<EventNode> rootItem = new TreeItem<>(rootEvent);
        treeView.setRoot(rootItem);

        addButton.setOnAction(e -> addEvent());
        editButton.setOnAction(e -> editEvent());
        deleteButton.setOnAction(e -> deleteEvent());
        calculateButton.setOnAction(e -> probabilityLabel.setText("Вероятность риска: " + calculateRisk(rootEvent)));

        VBox layout = new VBox(10, treeView, addButton, editButton, deleteButton, calculateButton, probabilityLabel);
        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Анализ дерева событий");
        primaryStage.show();
    }

    private void addEvent() {
        TreeItem<EventNode> selectedItem = treeView.getSelectionModel().getSelectedItem();

        // Если ничего не выбрано, добавляем в корень
        if (selectedItem == null) {
            selectedItem = treeView.getRoot();
        }

        EventDialog dialog = new EventDialog(null);
        TreeItem<EventNode> finalSelectedItem = selectedItem;
        dialog.showAndWait().ifPresent(newEvent -> {
            newEvent.setParent(finalSelectedItem.getValue());
            events.add(newEvent);
            finalSelectedItem.getChildren().add(new TreeItem<>(newEvent));
        });
    }

    private void editEvent() {
        TreeItem<EventNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            EventDialog dialog = new EventDialog(selectedItem.getValue());
            dialog.showAndWait().ifPresent(editedEvent -> {
                selectedItem.setValue(editedEvent);
            });
        }
    }

    private void deleteEvent() {
        TreeItem<EventNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getParent() != null) {
            selectedItem.getParent().getChildren().remove(selectedItem);
            events.remove(selectedItem.getValue());
        }
    }

    private double calculateRisk(EventNode node) {
        if (node.getType() == EventType.INIT) {
            return node.getProbability();  // Если это начальный узел, возвращаем вероятность
        }

        // Рекурсивно обрабатываем дочерние узлы
        double risk = (node.getType() == EventType.AND) ? 1.0 : 0.0;

        for (TreeItem<EventNode> childItem : treeView.getSelectionModel().getSelectedItem().getChildren()) {
            EventNode childNode = childItem.getValue();
            double childRisk = calculateRisk(childNode);  // Рекурсивный расчет риска для дочернего узла

            if (node.getType() == EventType.AND) {
                risk *= childRisk;  // Для типа AND умножаем риски
            } else {
                risk += childRisk;  // Для типа OR складываем риски
            }
        }

        return risk;
    }
}