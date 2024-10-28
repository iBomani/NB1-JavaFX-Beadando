package com.example.nb1javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.oanda.v20.oandaController;
import javafx.fxml.FXMLLoader;
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
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;

import java.sql.*;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

import javax.swing.*;

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

    @FXML
    public void szamlainformaciokItem(ActionEvent actionEvent){
        Stage newWindow = new Stage();
        BorderPane root = new BorderPane();


        final oandaController oc = new oandaController();
        Label label = new Label();
        label.setText(oc.skibidi().toString());
        VBox vBox = new VBox(label);
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
