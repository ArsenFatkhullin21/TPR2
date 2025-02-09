package ru.arsen.tpr2;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class EventDialog extends Dialog<EventNode> {
    private TextField nameField;
    private ComboBox<EventType> typeBox;
    private Spinner<Double> probSpinner;

    public EventDialog(EventNode event) {
        setTitle(event == null ? "Добавить событие" : "Редактировать событие");
        nameField = new TextField(event != null ? event.getName() : "");
        typeBox = new ComboBox<>();
        typeBox.getItems().addAll(EventType.values());
        typeBox.setValue(event != null ? event.getType() : EventType.INIT);
        probSpinner = new Spinner<>(0.0, 1.0, event != null ? event.getProbability() : 0.5, 0.01);

        VBox layout = new VBox(10, new Label("Название:"), nameField, new Label("Тип:"), typeBox, new Label("Вероятность:"), probSpinner);
        getDialogPane().setContent(layout);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                EventNode newNode = new EventNode(nameField.getText(), typeBox.getValue());
                newNode.setProbability(probSpinner.getValue());
                return newNode;
            }
            return null;
        });
    }
}
