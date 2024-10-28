package com.example.nb1javafx;

import csomag1.MNBArfolyamServiceSoapGetCurrenciesStringFaultFaultMessage;
import csomag1.MNBArfolyamServiceSoapGetInfoStringFaultFaultMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.oanda.v20.oandaController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.oanda.v20.account.AccountSummary;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;



import csomag1.MNBArfolyamServiceSoap;
import org.example.Main;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private final ObservableList<Labdarugo> filtered_playerList = FXCollections.observableArrayList();


    @FXML
    private MenuItem olvasItem;

    @FXML
    public void olvasItem() {
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);



        tableView = new TableView<>();
        loadDataFromDatabase(tableView);
        setupTableColumns();




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
                poszt = new Poszt(rs.getInt("id"), rs.getString("nev"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error fetching Poszt by ID: " + e.getMessage());
        }

        return poszt;
    }


    private String magyar;
    public void olvas2Item(ActionEvent actionEvent) {
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));


        TextField utonevField = new TextField();
        utonevField.setPromptText("Utónév (szűrés)");

        ComboBox<String> posztComboBox = new ComboBox<>();
        posztComboBox.setPromptText("Válasszon posztot");
        loadPosztok(posztComboBox);

        CheckBox ageCheckBox = new CheckBox("25 évnél fiatalabb");

        ToggleGroup magyarKulfoldiGroup = new ToggleGroup();
        RadioButton magyarRadio = new RadioButton("Magyar");
        RadioButton kulfoldiRadio = new RadioButton("Külföldi");
        magyarRadio.setToggleGroup(magyarKulfoldiGroup);
        kulfoldiRadio.setToggleGroup(magyarKulfoldiGroup);


        Button szuroButton = new Button("Szűrés");
        szuroButton.setOnAction(e -> {
            String utonev = utonevField.getText();
            String poszt = posztComboBox.getValue();

            if (magyarRadio.isSelected()) {
                magyar = "true";
            } else if (kulfoldiRadio.isSelected()) {
                magyar = "false";
            }

             // System.out.println("Magyar: " + magyar);
            boolean age = ageCheckBox.isSelected();



            loadFilteredDataFromDatabase(utonev, poszt, magyar, age);
        });

        formLayout.getChildren().addAll(
                new Label("Adatok szűrése:"),
                utonevField,
                posztComboBox,
                magyarRadio,
                kulfoldiRadio,
                ageCheckBox,
                szuroButton
        );

        tableView = new TableView<>();
        setupTableColumns();

        root.setTop(formLayout);
        root.setCenter(tableView);

        newWindow.setTitle("2. feladat - Olvas2");
        newWindow.setScene(scene);
        newWindow.show();
    }


    private void loadPosztok(ComboBox<String> posztComboBox) {

        Connection conn = DatabaseConnection.connect();
        String sql = "SELECT nev FROM poszt";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                posztComboBox.getItems().add(rs.getString("nev"));
            }
        } catch (SQLException e) {
            System.out.println("Hiba a posztok betöltésekor: " + e.getMessage());
        }
    }
    private void setupTableColumns() {



        idCol = new TableColumn<>("ID");
        utonevCol = new TableColumn<>("Utónév");
        vezeteknevCol = new TableColumn<>("Vezetéknév");
        szulidoCol = new TableColumn<>("Születési dátum");
        magyarCol = new TableColumn<>("Magyar");
        kulfoldiCol = new TableColumn<>("Külföldi");
        mezszamCol = new TableColumn<>("Mezszám");
        klubidCol = new TableColumn<>("Klub név");
        posztIdCol = new TableColumn<>("Poszt");
        ertekCol = new TableColumn<>("Érték");


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


        tableView.getColumns().addAll(idCol, vezeteknevCol, utonevCol, szulidoCol, magyarCol, kulfoldiCol,  mezszamCol, klubidCol, posztIdCol, ertekCol);
    }
    private void loadFilteredDataFromDatabase(String utonev, String poszt, String magyar, boolean age) {

        StringBuilder sql = new StringBuilder("SELECT l.id, l.mezszam, l.utonev, l.vezeteknev, l.szulido, l.magyar, l.kulfoldi, l.ertek, p.id AS posztid, k.id AS klubid " +
                "FROM labdarugo l " +
                "JOIN poszt p ON l.posztid = p.id " +
                "JOIN klub k ON l.klubid = k.id WHERE 1=1"); // Alapértelmezett feltétel

        if (utonev != null && !utonev.isEmpty()) {
            sql.append(" AND l.utonev LIKE '%").append(utonev).append("%'");
        }
        if (poszt != null) {
            sql.append(" AND p.nev = '").append(poszt).append("'");
        }

        if (magyar == "true") {
            sql.append(" AND (l.magyar = -1 OR l.magyar = 1)");
        } else if (magyar == "false") {
            sql.append(" AND (l.kulfoldi = -1 OR l.kulfoldi = 1)");
        } else {
            sql.append(" AND (l.magyar = -1 OR l.kulfoldi = -1 OR l.magyar = 1 OR l.kulfoldi = 1)");
        }
        if(age) {
            sql.append(" AND l.szulido > DATE('now', '-25 years')");
        }

        System.out.println("SQL: " + sql.toString());

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

            filtered_playerList.clear(); //
            while (rs.next()) {
                int id = rs.getInt("id");
                int mezszam = rs.getInt("mezszam");
                String utonevValue = rs.getString("utonev");
                String vezeteknev = rs.getString("vezeteknev");
                String szulido = rs.getString("szulido");
                boolean magyarValue = rs.getBoolean("magyar");
                boolean kulfoldi = rs.getBoolean("kulfoldi");
                int ertek = rs.getInt("ertek");
                int posztid = rs.getInt("posztid");
                int klubid = rs.getInt("klubid");

                Klub klub = fetchKlubById(klubid);
                String klubNev = klub.getNev();
                Poszt posztObj = fetchPosztById(posztid);
                String posztNev = posztObj.getNev();


                filtered_playerList.add(new Labdarugo(id, mezszam, posztNev, utonevValue, vezeteknev, szulido, magyarValue, kulfoldi, ertek, klubNev));
            }
        } catch (SQLException e) {
            System.out.println("Hiba a szűrt adatok betöltésekor: " + e.getMessage());
        }


        tableView.setItems(filtered_playerList);
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

    public void populateIdComboBox(ComboBox<Integer> comboBox) {
        comboBox.getItems().clear();
        Connection conn = DatabaseConnection.connect();
        String sql = "SELECT id FROM labdarugo";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                if (!comboBox.getItems().contains(id)) {
                    comboBox.getItems().add(id);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error populating ComboBox with IDs: " + e.getMessage());
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
        Scene scene = new Scene(root, 400, 200);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));


        ComboBox<Integer> idComboBox = new ComboBox<>();
        idComboBox.setPromptText("Válasszon azonosítót törléshez");


        idComboBox.getItems().clear();
        populateIdComboBox(idComboBox);


        Button deleteButton = new Button("Törlés");
        deleteButton.setDisable(true);


        idComboBox.setOnAction(event -> {
            deleteButton.setDisable(idComboBox.getValue() == null);
        });


        deleteButton.setOnAction(event -> {
            int selectedId = idComboBox.getValue();
            deleteRecordFromDatabase(selectedId);
            idComboBox.getItems().remove(Integer.valueOf(selectedId));
        });


        layout.getChildren().addAll(
                new Label("Rekord törlése:"),
                idComboBox, deleteButton
        );
        root.setCenter(layout);

        newWindow.setTitle("5. feladat - Töröl");
        newWindow.setScene(scene);
        newWindow.show();
    }



    private void deleteRecordFromDatabase(int id) {
        String sql = "DELETE FROM labdarugo WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record with ID " + id + " deleted successfully.");
                refreshTable();
            } else {
                System.out.println("No record found with ID " + id + ".");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    private void refreshTable() {
        tableView.getItems().clear();
    }




    @FXML
    public void letoltesItem(ActionEvent actionEvent) throws MNBArfolyamServiceSoapGetInfoStringFaultFaultMessage {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        Label titleLabel = new Label("Valuta letöltés");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        titleLabel.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Elérhető valuták, 2 heti árfolyamok és valuta egységek letöltése.");
        instructionLabel.setFont(Font.font("Arial", 16));
        instructionLabel.setTextFill(Color.GRAY);

        Button downloadButton = new Button("Letöltés");
        downloadButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        downloadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8px 16px;");
        downloadButton.setOnAction(event -> {
            try {
                MNBArfolyamServiceSoap service = Main.getService();
                String currenciesXML = service.getCurrencies();

                LocalDate today = LocalDate.now();
                LocalDate twoWeeksBefore = today.minusWeeks(2);
                String exchangeRatesXML = service.getExchangeRates(twoWeeksBefore.toString(), today.toString(), "EUR");
                String currencyUnitsXML = service.getCurrencyUnits("HUF");

                String currencies = formatCurrencies(currenciesXML);
                String exchangeRates = formatExchangeRates(exchangeRatesXML);
                String currencyUnits = formatCurrencyUnits(currencyUnitsXML);

                String data = "Currencies:\n" + currencies + "\n\n" +
                        "Current Exchange Rates:\n" + exchangeRates + "\n\n" +
                        "Currency Units:\n" + currencyUnits + "\n\n";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
                    writer.write(data);
                }

                instructionLabel.setText("Adatok sikeresen letöltve: data.txt");
            } catch (Exception e) {
                instructionLabel.setText("Error: " + e.toString());
            }
        });

        VBox contentBox = new VBox(20, titleLabel, instructionLabel, downloadButton);
        contentBox.setPadding(new Insets(30, 50, 30, 50));
        contentBox.setAlignment(Pos.CENTER);

        root.setCenter(contentBox);
        root.setStyle("-fx-background-color: #F5F5F5; -fx-border-width: 2px; -fx-border-color: #DDDDDD;");

        newWindow.setTitle("2. feladat - Letöltés");
        newWindow.setScene(scene);
        newWindow.show();
    }

    public void letoltes2Item(ActionEvent actionEvent) throws MNBArfolyamServiceSoapGetCurrenciesStringFaultFaultMessage {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("2. feladat - Letöltés2");

        Label label = new Label("Fájl neve:");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.DARKSLATEBLUE);

        TextArea textArea = new TextArea();
        textArea.setMaxSize(300, 25);
        textArea.setFont(Font.font("Arial", 14));
        textArea.setPromptText("Adja meg a fájl nevét");

        ComboBox<String> currencyField = new ComboBox<>();
        currencyField.getItems().addAll(getCurrencies());
        currencyField.setPromptText("Valuta");
        currencyField.setMaxWidth(300);
        currencyField.setStyle("-fx-font-size: 14px;");

        DatePicker fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("Mettől");
        fromDatePicker.setStyle("-fx-font-size: 14px;");
        fromDatePicker.setMaxWidth(300);

        DatePicker toDatePicker = new DatePicker();
        toDatePicker.setPromptText("Meddig");
        toDatePicker.setStyle("-fx-font-size: 14px;");
        toDatePicker.setMaxWidth(300);

        Button saveButton = new Button("Adatok letöltése");
        saveButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8px 16px;");
        saveButton.setOnAction(e -> saveData(textArea.getText(), currencyField.getValue(), fromDatePicker.getValue(), toDatePicker.getValue()));

        VBox vbox = new VBox(15, label, textArea, currencyField, fromDatePicker, toDatePicker, saveButton);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #F9F9F9;");

        Scene scene = new Scene(vbox, 400, 350);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void saveData(String filename, String value, LocalDate fromDatePickerValue, LocalDate toDatePickerValue) {
            MNBArfolyamServiceSoap service = Main.getService();
            try {
                System.out.println(fromDatePickerValue.toString());
                System.out.println(toDatePickerValue.toString());
                System.out.println(value);
                String exchangeRatesXML = service.getExchangeRates(fromDatePickerValue.toString(), toDatePickerValue.toString(), value);
                System.out.println(exchangeRatesXML);
                String currencyUnitsXML = service.getCurrencyUnits(value);

                String exchangeRates = formatExchangeRates(exchangeRatesXML);
                String currencyUnits = formatCurrencyUnits(currencyUnitsXML);

                String data = "HUF - " + value + "\n\n" + "Árfolyamok::\n" + exchangeRates + "\n\n" +
                        "Valuta egység:\n" + currencyUnits + "\n\n";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".txt"))) {
                    writer.write(data);
                    writer.flush();
                }

                System.out.println("Adatok sikeresen letöltve: " + filename);
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
    }


    public void grafikonItem(ActionEvent actionEvent) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("2. feladat - Letöltés2");

        Label label = new Label("Árfolyam grafikon");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.DARKSLATEBLUE);


        ComboBox<String> currencyField = new ComboBox<>();
        currencyField.getItems().addAll(getCurrencies());
        currencyField.setPromptText("Valuta");
        currencyField.setMaxWidth(300);
        currencyField.setStyle("-fx-font-size: 14px;");

        DatePicker fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("Mettől");
        fromDatePicker.setStyle("-fx-font-size: 14px;");
        fromDatePicker.setMaxWidth(300);

        DatePicker toDatePicker = new DatePicker();
        toDatePicker.setPromptText("Meddig");
        toDatePicker.setStyle("-fx-font-size: 14px;");
        toDatePicker.setMaxWidth(300);

        Button saveButton = new Button("Grafikon megjelenítése");
        saveButton.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8px 16px;");
        saveButton.setOnAction(e -> showGraph(toDictionary(currencyField.getValue(), fromDatePicker.getValue(), toDatePicker.getValue())));

        VBox vbox = new VBox(15, label, currencyField, fromDatePicker, toDatePicker, saveButton);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #F9F9F9;");

        Scene scene = new Scene(vbox, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showGraph(Dictionary<String, Double> dictionary) {
        Stage graphStage = new Stage();
        graphStage.setTitle("2. feladat - Árfolyam grafikon");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Dátum");

        Enumeration<Double> rates = dictionary.elements();
        double minRate = Collections.min(Collections.list(rates));
        double maxRate = Collections.max(Collections.list(dictionary.elements()));

        NumberAxis yAxis = new NumberAxis(minRate - 1, maxRate + 1, 0.2);
        yAxis.setLabel("Árfolyam");
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Árfolyam grafikon");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Árfolyam");


        dictionary.keys().asIterator().forEachRemaining(date -> {
            series.getData().add(new XYChart.Data<>(date, dictionary.get(date)));
        });

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        graphStage.setScene(scene);
        graphStage.show();
    }

    private Dictionary<String, Double> toDictionary(String currency, LocalDate fromDate, LocalDate toDate) {
        MNBArfolyamServiceSoap service = Main.getService();
        Dictionary<String, Double> dictionary = new Hashtable<>();
        try {
            String exchangeRatesXML = service.getExchangeRates(fromDate.toString(), toDate.toString(), currency);

            String exchangeRates = formatExchangeRates(exchangeRatesXML);

            String[] lines = exchangeRates.split("\n");


            for (String line : lines) {
                String date = line.substring(line.indexOf("Date: ") + 6, line.indexOf("\", Rate:"));
                String rateStr = line.substring(line.indexOf(">") + 1).trim();
                rateStr = rateStr.replace(",", ".");

                double rate = Double.parseDouble(rateStr);
                dictionary.put(date, rate);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

        return dictionary;
    }


    public List<String> getCurrencies() {
        List<String> currencies = new ArrayList<>();
        try {
            MNBArfolyamServiceSoap service = Main.getService();
            String xml = service.getCurrencies();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

                NodeList nodeList = doc.getElementsByTagName("Curr");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    currencies.add(nodeList.item(i).getTextContent());
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                throw new RuntimeException(e);
            }
        } catch (MNBArfolyamServiceSoapGetCurrenciesStringFaultFaultMessage e) {
            e.printStackTrace();
        }
        currencies.remove("HUF");
        return currencies;
    }



    private String formatCurrencyUnits(String xml) {
        StringBuilder formattedUnits = new StringBuilder();
        String[] units = xml.split("<Unit curr=\"");
        for (String unit : units) {
            if (unit.contains("</Unit>")) {
                String currency = unit.substring(0, unit.indexOf("\">"));
                String value = unit.substring(unit.indexOf("\">") + 2, unit.indexOf("</Unit>"));
                formattedUnits.append("Currency: ").append(currency).append(", Unit: ").append(value).append("\n");
            }
        }
        return formattedUnits.toString();
    }

    private String formatExchangeRates(String xml) {
        StringBuilder formattedRates = new StringBuilder();
        String[] rates = xml.split("<Day date=\"");
        for (String rate : rates) {
            if (rate.contains("<Rate")) {
                String date = rate.substring(0, rate.indexOf(">"));
                String value = rate.substring(rate.indexOf(">") + 1, rate.indexOf("</Rate>"));
                formattedRates.append("Date: ").append(date).append(", Rate: ").append(value).append("\n");
            }
        }
        return formattedRates.toString();
    }

    private String formatCurrencies(String xml) {
        StringBuilder formattedCurrencies = new StringBuilder();

        formattedCurrencies.append("Available Currencies: ");
        String[] currencies = xml.split("<Curr>");
        for (String currency : currencies) {
            if (currency.contains("</Curr>")) {
                formattedCurrencies.append(currency.replace("</Curr>", "").trim()).append(", ");
            }
        }

        if (formattedCurrencies.length() > 0) {
            formattedCurrencies.setLength(formattedCurrencies.length() - 2);
        }
        return formattedCurrencies.toString();
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

    @FXML
    public void szamlainformaciokItem(ActionEvent actionEvent){
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();

        final oandaController oc = new oandaController();
        AccountSummary accountSummary = oc.skibidi();

        VBox vBox = new VBox(15);
        vBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Label headerLabel = new Label(" -- Számla információk -- ");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label idLabel = new Label("ID: " + accountSummary.getId());
        Label aliasLabel = new Label("Alias: " + accountSummary.getAlias());
        Label currencyLabel = new Label("Currency: " + accountSummary.getCurrency());
        Label balanceLabel = new Label("Balance: " + accountSummary.getBalance());
        Label createdByUserIDLabel = new Label("Created By User ID: " + accountSummary.getCreatedByUserID());
        Label createdTimeLabel = new Label("Created Time: " + accountSummary.getCreatedTime());

        idLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        aliasLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        currencyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        createdByUserIDLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        createdTimeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        vBox.getChildren().addAll(headerLabel, idLabel, aliasLabel, currencyLabel, balanceLabel, createdByUserIDLabel, createdTimeLabel);

        Scene scene = new Scene(vBox, 600, 400);
        newWindow.setTitle("4. feladat - Számlainformációk");
        newWindow.setScene(scene);
        newWindow.show();
    }
    @FXML
    public void aktualisarakItem(ActionEvent actionEvent){
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();


        final oandaController oc = new oandaController();
        Label label = new Label();
        label.setText(oc.aktualisarak());
        VBox vBox = new VBox(label);
        Scene scene = new Scene(vBox, 600, 400);
        newWindow.setTitle("4. feladat - Aktuálisárak");
        newWindow.setScene(scene);
        newWindow.show();
    }
    @FXML
    public void historikusarakItem(ActionEvent actionEvent){

    }
    @FXML
    public void pozicionyitasItem(ActionEvent actionEvent){

    }
    @FXML
    public void poziciozarasItem(ActionEvent actionEvent){

    }
    @FXML
    public void nyitottpoziciokItem(ActionEvent actionEvent){

    }

}
