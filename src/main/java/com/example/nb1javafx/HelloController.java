package com.example.nb1javafx;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private MenuItem olvasItem;

    @FXML
    public void olvasItem() {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        Label label = new Label("Hello GAMF!");
        root.setCenter(label);

        newWindow.setTitle("1. feladat - Olvas");
        newWindow.setScene(scene);
        newWindow.show();
    }


}
