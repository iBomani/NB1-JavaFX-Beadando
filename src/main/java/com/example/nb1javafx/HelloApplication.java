package com.example.nb1javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Objects;

public class    HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));

        Label welcomeLabel = new Label("Üdvözöljük a JavaFX alkalmazásunkban!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: green; -fx-font-weight: bold; -fx-font-family: Arial;");
        welcomeLabel.setLayoutX(150);
        welcomeLabel.setLayoutY(150);


        Pane pane = new Pane();
        pane.getChildren().addAll(root, welcomeLabel);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("JavaFX Beadandó - Terék Péter & Kosztándi Róbert");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
