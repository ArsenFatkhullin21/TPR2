module ru.arsen.tpr2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.arsen.tpr2 to javafx.fxml;
    exports ru.arsen.tpr2;
}