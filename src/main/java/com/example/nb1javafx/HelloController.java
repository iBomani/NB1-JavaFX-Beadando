package com.example.nb1javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import soapmnb.MNBArfolyamServiceSoap;
import soapmnb.MNBArfolyamServiceSoapImpl;

import javax.xml.ws.soap.SOAPFaultException;
import java.sql.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

public class HelloController {
    @FXML
    private TableView<Labdarugo> tableView;

    @FXML
    private TableColumn<Labdarugo, Integer> idCol;

    @FXML
    private TableColumn<Labdarugo, String> utonevCol;

    @FXML
    private TableColumn<Labdarugo, String> vezeteknevCol;

    @FXML
    private TableColumn<Labdarugo, String> szulidoCol;

    @FXML
    private TableColumn<Labdarugo, Boolean> magyarCol;

    @FXML
    private TableColumn<Labdarugo, Boolean> kulfoldiCol;

    @FXML
    private TableColumn<Labdarugo, Integer> ertekCol;
    @FXML
    private TableColumn<Labdarugo, String> mezszamCol;
    @FXML
    private TableColumn<Labdarugo, String> klubidCol;

    @FXML
    private TableColumn<Labdarugo, String> posztIdCol;

    private final ObservableList<Labdarugo> playerList = FXCollections.observableArrayList();
    @FXML
    private MenuItem olvasItem;

    @FXML
    public void olvasItem() {
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        tableView = new TableView<>();

        // Oszlopok létrehozása
        idCol = new TableColumn<>("ID");
        mezszamCol = new TableColumn<>("Mezszám:");
        klubidCol = new TableColumn<>("KlubId:");
        utonevCol = new TableColumn<>("Utónév");
        vezeteknevCol = new TableColumn<>("Vezetéknév");
        szulidoCol = new TableColumn<>("Születési dátum");
        magyarCol = new TableColumn<>("Magyar");
        kulfoldiCol = new TableColumn<>("Külföldi");
        ertekCol = new TableColumn<>("Érték");

        posztIdCol = new TableColumn<>("PosztId");

        // Oszlopok értékekhez kötése
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mezszamCol.setCellValueFactory(new PropertyValueFactory<>("mezszam"));
        klubidCol.setCellValueFactory(new PropertyValueFactory<>("klubneve"));
        utonevCol.setCellValueFactory(new PropertyValueFactory<>("utonev"));
        vezeteknevCol.setCellValueFactory(new PropertyValueFactory<>("vezeteknev"));
        szulidoCol.setCellValueFactory(new PropertyValueFactory<>("szulido"));
        magyarCol.setCellValueFactory(new PropertyValueFactory<>("magyar"));
        kulfoldiCol.setCellValueFactory(new PropertyValueFactory<>("kulfoldi"));
        ertekCol.setCellValueFactory(new PropertyValueFactory<>("ertek"));

        posztIdCol.setCellValueFactory(new PropertyValueFactory<>("posztneve"));

        // Oszlopok hozzáadása a táblázathoz
        tableView.getColumns().addAll(idCol, mezszamCol,klubidCol,utonevCol, vezeteknevCol, szulidoCol, magyarCol, kulfoldiCol, ertekCol, posztIdCol);

        // Adatok betöltése
        loadDataFromDatabase(tableView);

        // Táblázat adatokkal
        tableView.setItems(playerList);

        root.setCenter(tableView);
        newWindow.setTitle("Olvas - Adatok megjelenítése");
        newWindow.setScene(scene);
        newWindow.show();
    }

    public void loadDataFromDatabase(TableView<Labdarugo> tableView) {
        Connection conn = DatabaseConnection.connect();
        String sql = "SELECT l.id, l.mezszam, l.utonev, l.vezeteknev, l.szulido, l.magyar, l.kulfoldi, l.ertek, p.id AS posztid, k.id AS klubid " +
                "FROM labdarugo l " +
                "JOIN poszt p ON l.posztid = p.id " +
                "JOIN klub k ON l.klubid = k.id";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Az adatok beolvasása és hozzáadása a TableView-hez

            while (rs.next()) {
                int id = rs.getInt("id");
                int mezszam = rs.getInt("mezszam");
                int posztid = rs.getInt("posztid");
                String utonev = rs.getString("utonev");
                String vezeteknev = rs.getString("vezeteknev");
                String szulido = rs.getString("szulido");
                boolean magyar = rs.getBoolean("magyar");
                boolean kulfoldi = rs.getBoolean("kulfoldi");
                int ertek = rs.getInt("ertek");
                int klubid = rs.getInt("klubid");



                Poszt poszt = fetchPosztById(posztid);
                String posztNev = poszt.getNev();
                Klub klub = fetchKlubById(klubid);
                String klubNev = klub.getNev();

                /* ----- debugolasra voltak ezek
                System.out.println("ID: " + id + ", Mezszam: " + mezszam + ", PosztId: " + posztid + ", KlubId: " + klubid);
                System.out.println("Klub: " + klubNev);
                System.out.println("Poszt: " + posztNev);
                */

                playerList.add(new Labdarugo(id, mezszam, posztNev, utonev, vezeteknev, szulido, magyar,kulfoldi, ertek, klubNev));
            }

            tableView.setItems(playerList);
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Hiba az adatok betöltése során: " + e.getMessage());
        }
    }

    private Klub fetchKlubById(int klubid) {
        Connection conn = DatabaseConnection.connect();
        Klub klub = null;
        String sql = "SELECT * FROM klub WHERE id = ?";


        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, klubid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                klub = new Klub(rs.getInt("id"), rs.getString("csapatnev"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error fetching Klub by ID: " + e.getMessage());
        }

        return klub;
    }

    private Poszt fetchPosztById(int posztid) {
        Connection conn = DatabaseConnection.connect();
        Poszt poszt = null;
        String sql = "SELECT * FROM poszt WHERE id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, posztid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                poszt = new Poszt(rs.getInt("id"), rs.getString("nev")); // Adjust the column names as needed
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error fetching Poszt by ID: " + e.getMessage());
        }

        return poszt;
    }

    public void olvas2Item(ActionEvent actionEvent) {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        Label label = new Label("Hello GAMF!");
        root.setCenter(label);

        newWindow.setTitle("2. feladat - Olvas2");
        newWindow.setScene(scene);
        newWindow.show();
    }

    public void irItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));


        TextField mezszamField = new TextField();
        mezszamField.setPromptText("Mezszám");

        TextField utonevField = new TextField();
        utonevField.setPromptText("Utónév");

        TextField vezeteknevField = new TextField();
        vezeteknevField.setPromptText("Vezetéknév");

        TextField szulidoField = new TextField();
        szulidoField.setPromptText("Születési dátum (YYYY-MM-DD)");

        CheckBox magyarCheckBox = new CheckBox("Magyar");
        CheckBox kulfoldiCheckBox = new CheckBox("Külföldi");

        TextField ertekField = new TextField();
        ertekField.setPromptText("Érték");

        TextField klubidField = new TextField();
        klubidField.setPromptText("Klub ID");

        TextField posztidField = new TextField();
        posztidField.setPromptText("Poszt ID");

        Button submitButton = new Button("Hozzáadás");


        formLayout.getChildren().addAll(
                new Label("Új Labdarugó hozzáadása:"),
                mezszamField, utonevField, vezeteknevField,
                szulidoField, magyarCheckBox, kulfoldiCheckBox,
                ertekField, klubidField, posztidField, submitButton
        );

        root.setCenter(formLayout);


        submitButton.setOnAction(event -> {
            int mezszam = Integer.parseInt(mezszamField.getText());
            String utonev = utonevField.getText();
            String vezeteknev = vezeteknevField.getText();
            String szulido = szulidoField.getText();
            boolean magyar = magyarCheckBox.isSelected();
            boolean kulfoldi = kulfoldiCheckBox.isSelected();
            int ertek = Integer.parseInt(ertekField.getText());
            int klubid = Integer.parseInt(klubidField.getText());
            int posztid = Integer.parseInt(posztidField.getText());


            insertLabdarugoToDatabase(mezszam, utonev, vezeteknev, szulido, magyar, kulfoldi, ertek, klubid, posztid);


            mezszamField.clear();
            utonevField.clear();
            vezeteknevField.clear();
            szulidoField.clear();
            magyarCheckBox.setSelected(false);
            kulfoldiCheckBox.setSelected(false);
            ertekField.clear();
            klubidField.clear();
            posztidField.clear();
        });

        newWindow.setTitle("3. feladat - Ír");
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void insertLabdarugoToDatabase(int mezszam, String utonev, String vezeteknev, String szulido,
                                           boolean magyar, boolean kulfoldi, int ertek, int klubid, int posztid) {
        String sql = "INSERT INTO labdarugo (mezszam, utonev, vezeteknev, szulido, magyar, kulfoldi, ertek, klubid, posztid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mezszam);
            pstmt.setString(2, utonev);
            pstmt.setString(3, vezeteknev);
            pstmt.setString(4, szulido);
            pstmt.setBoolean(5, magyar);
            pstmt.setBoolean(6, kulfoldi);
            pstmt.setInt(7, ertek);
            pstmt.setInt(8, klubid);
            pstmt.setInt(9, posztid);

            pstmt.executeUpdate();
            System.out.println("New record added successfully.");

        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }


    public void modositItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));


        ComboBox<Integer> idComboBox = new ComboBox<>();
        idComboBox.setPromptText("Válasszon azonosítót");


        populateIdComboBox(idComboBox);


        TextField mezszamField = new TextField();
        mezszamField.setPromptText("Mezszám");

        TextField utonevField = new TextField();
        utonevField.setPromptText("Utónév");

        TextField vezeteknevField = new TextField();
        vezeteknevField.setPromptText("Vezetéknév");

        TextField szulidoField = new TextField();
        szulidoField.setPromptText("Születési dátum (YYYY-MM-DD)");

        CheckBox magyarCheckBox = new CheckBox("Magyar");
        CheckBox kulfoldiCheckBox = new CheckBox("Külföldi");

        TextField ertekField = new TextField();
        ertekField.setPromptText("Érték");

        TextField klubidField = new TextField();
        klubidField.setPromptText("Klub ID");

        TextField posztidField = new TextField();
        posztidField.setPromptText("Poszt ID");

        Button submitButton = new Button("Módosítás");


        idComboBox.setOnAction(event -> {
            int selectedId = idComboBox.getValue();
            loadRecordDetails(selectedId, mezszamField, utonevField, vezeteknevField, szulidoField, magyarCheckBox, kulfoldiCheckBox, ertekField, klubidField, posztidField);
        });


        formLayout.getChildren().addAll(
                new Label("Rekord módosítása:"),
                idComboBox, mezszamField, utonevField, vezeteknevField,
                szulidoField, magyarCheckBox, kulfoldiCheckBox,
                ertekField, klubidField, posztidField, submitButton
        );

        root.setCenter(formLayout);


        submitButton.setOnAction(event -> {
            int id = idComboBox.getValue();
            int mezszam = Integer.parseInt(mezszamField.getText());
            String utonev = utonevField.getText();
            String vezeteknev = vezeteknevField.getText();
            String szulido = szulidoField.getText();
            boolean magyar = magyarCheckBox.isSelected();
            boolean kulfoldi = kulfoldiCheckBox.isSelected();
            int ertek = Integer.parseInt(ertekField.getText());
            int klubid = Integer.parseInt(klubidField.getText());
            int posztid = Integer.parseInt(posztidField.getText());

            updateRecordInDatabase(id, mezszam, utonev, vezeteknev, szulido, magyar, kulfoldi, ertek, klubid, posztid);
        });

        newWindow.setTitle("4. feladat - Módosít");
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void populateIdComboBox(ComboBox<Integer> idComboBox) {
        String sql = "SELECT id FROM labdarugo";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                idComboBox.getItems().add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error populating ComboBox: " + e.getMessage());
        }
    }

    private void loadRecordDetails(int id, TextField mezszamField, TextField utonevField, TextField vezeteknevField,
                                   TextField szulidoField, CheckBox magyarCheckBox, CheckBox kulfoldiCheckBox,
                                   TextField ertekField, TextField klubidField, TextField posztidField) {
        String sql = "SELECT * FROM labdarugo WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                mezszamField.setText(String.valueOf(rs.getInt("mezszam")));
                utonevField.setText(rs.getString("utonev"));
                vezeteknevField.setText(rs.getString("vezeteknev"));
                szulidoField.setText(rs.getString("szulido"));
                magyarCheckBox.setSelected(rs.getBoolean("magyar"));
                kulfoldiCheckBox.setSelected(rs.getBoolean("kulfoldi"));
                ertekField.setText(String.valueOf(rs.getInt("ertek")));
                klubidField.setText(String.valueOf(rs.getInt("klubid")));
                posztidField.setText(String.valueOf(rs.getInt("posztid")));
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Error loading record details: " + e.getMessage());
        }
    }

    private void updateRecordInDatabase(int id, int mezszam, String utonev, String vezeteknev, String szulido,
                                        boolean magyar, boolean kulfoldi, int ertek, int klubid, int posztid) {
        String sql = "UPDATE labdarugo SET mezszam = ?, utonev = ?, vezeteknev = ?, szulido = ?, magyar = ?, kulfoldi = ?, " +
                "ertek = ?, klubid = ?, posztid = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mezszam);
            pstmt.setString(2, utonev);
            pstmt.setString(3, vezeteknev);
            pstmt.setString(4, szulido);
            pstmt.setBoolean(5, magyar);
            pstmt.setBoolean(6, kulfoldi);
            pstmt.setInt(7, ertek);
            pstmt.setInt(8, klubid);
            pstmt.setInt(9, posztid);
            pstmt.setInt(10, id);

            pstmt.executeUpdate();
            System.out.println("Record updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }


    public void torolItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        Label label = new Label("Hello GAMF!");
        root.setCenter(label);

        newWindow.setTitle("5. feladat - Töröl");
        newWindow.setScene(scene);
        newWindow.show();
    }

    @FXML
    public void letoltesItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();

        try {
            System.out.println("VALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMIVALAMI AVLAMI");
            String resp = service.getExchangeRates("2021-04-01", "2021-04-30", "EUR");
            Label label = new Label(resp);
            root.setCenter(label);

        } catch (SOAPFaultException e) {
            System.out.println("SOAPFaultException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }


        newWindow.setTitle("2. feladat - Letöltés");
        newWindow.setScene(scene);
        newWindow.show();
    }

    @FXML
    public void parhuzamosItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();


        BorderPane root = new BorderPane();
        VBox vBox = new VBox(15);
        vBox.setPadding(new Insets(20));


        Label label1 = new Label("Label 1: Hello Dános!");
        label1.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label1.setTextFill(Color.DARKBLUE);

        Label label2 = new Label("Label 2: Hello Szele!");
        label2.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label2.setTextFill(Color.DARKGREEN);

        Button button = new Button("Start!");
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle("-fx-background-color: lightgray; -fx-padding: 10px;");

        button.setOnAction(event -> startUpdatingLabels(label1, label2));


        vBox.getChildren().addAll(label1, label2, button);


        Scene scene = new Scene(vBox, 600, 400);
        newWindow.setTitle("3. feladat - Párhuzamos");
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void startUpdatingLabels(Label label1, Label label2) {
        Thread thread1 = new Thread(() -> {
            String[] texts1 = {"Tápiószele!", "Dános!", "Dánszentmiklós!", "Ford!"};
            int index = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                final String text = texts1[index % texts1.length];
                index++;
                javafx.application.Platform.runLater(() -> label1.setText("Label 1: " + text));
            }
        });

        Thread thread2 = new Thread(() -> {
            String[] texts2 = {"Skoda!", "Tesla!", "Kroko!", "JAVA!"};
            int index = 0;
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                final String text = texts2[index % texts2.length];
                index++;
                javafx.application.Platform.runLater(() -> label2.setText("Label 2: " + text));
            }
        });

        thread1.setDaemon(true);
        thread2.setDaemon(true);
        thread1.start();
        thread2.start();
    }



}
