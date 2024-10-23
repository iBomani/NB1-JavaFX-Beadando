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


        // ADATBAZIS MENU
        Menu adatbazisMenu = new Menu("Adatbázis");

        MenuItem olvasItem = new MenuItem("Olvas");
        olvasItem.setOnAction(event -> olvasItem());

        MenuItem olvas2Item = new MenuItem("Olvas2");
        MenuItem irItem = new MenuItem("Ír");
        MenuItem modositItem = new MenuItem("Módosít");
        MenuItem torolItem = new MenuItem("Töröl");

        adatbazisMenu.getItems().addAll(olvasItem, olvas2Item, irItem, modositItem, torolItem);

        menuBar.getMenus().add(adatbazisMenu);

        // SOAPKLIENS MENU
        Menu soapKliensMenu = new Menu("SoapKliens");

        MenuItem letoltesItem = new MenuItem("Letöltés");
        MenuItem letoltes2Item = new MenuItem("Letöltés2");
        MenuItem grafikonItem = new MenuItem("Grafikon");

        soapKliensMenu.getItems().addAll(letoltesItem, letoltes2Item, grafikonItem);

        menuBar.getMenus().add(soapKliensMenu);

        // PARHUZAMOS MENU
        Menu parhuzamosMenu = new Menu("Párhuzamos");

        menuBar.getMenus().add(parhuzamosMenu);

        // FOREX MENU
        Menu forexMenu = new Menu("Forex");

        MenuItem szamlaItem = new MenuItem("Számlainformációk");
        MenuItem arakItem = new MenuItem("Aktuális árak");
        MenuItem histArakItem = new MenuItem("Historikus árak");
        MenuItem pozicioNyitasItem = new MenuItem("Pozíció nyitás");
        MenuItem pozicioZarasItem = new MenuItem("Pozíció zárás");
        MenuItem nyitottPozItem = new MenuItem("Nyitott pozíciók");

        forexMenu.getItems().addAll(szamlaItem, arakItem, histArakItem, pozicioNyitasItem, pozicioZarasItem, nyitottPozItem);

        menuBar.getMenus().add(forexMenu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("JavaFX Beadandó - Terék Péter & Kosztándi Róbert");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void olvasItem() {
        Stage newWindow = new Stage();


        BorderPane root = new BorderPane();

        Scene scene = new Scene(root, 600, 400);


        newWindow.setTitle("1. feladat - Olvas");
        newWindow.setScene(scene);

        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}