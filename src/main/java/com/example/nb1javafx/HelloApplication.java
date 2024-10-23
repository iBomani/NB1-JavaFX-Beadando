package com.example.nb1javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();


        Menu adatbazisMenu = new Menu("WE CAN GO AURA FOR AURA");


        MenuItem olvasItem = new MenuItem("Olvas");
        MenuItem olvas2Item = new MenuItem("Olvas2");
        MenuItem irItem = new MenuItem("Ír");
        MenuItem modositItem = new MenuItem("Módosít");
        MenuItem torolItem = new MenuItem("Töröl");


        adatbazisMenu.getItems().addAll(olvasItem, olvas2Item, irItem, modositItem, torolItem);


        menuBar.getMenus().add(adatbazisMenu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("JavaFX CRUD alkalmazás");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}