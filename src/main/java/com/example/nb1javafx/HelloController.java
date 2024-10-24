package com.example.nb1javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import soapmnb.MNBArfolyamServiceSoap;
import soapmnb.MNBArfolyamServiceSoapGetCurrenciesStringFaultFaultMessage;
import soapmnb.MNBArfolyamServiceSoapGetInfoStringFaultFaultMessage;
import soapmnb.MNBArfolyamServiceSoapImpl;

import javax.xml.ws.soap.SOAPFaultException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        klubidCol.setCellValueFactory(new PropertyValueFactory<>("klubid"));
        utonevCol.setCellValueFactory(new PropertyValueFactory<>("utonev"));
        vezeteknevCol.setCellValueFactory(new PropertyValueFactory<>("vezeteknev"));
        szulidoCol.setCellValueFactory(new PropertyValueFactory<>("szulido"));
        magyarCol.setCellValueFactory(new PropertyValueFactory<>("magyar"));
        kulfoldiCol.setCellValueFactory(new PropertyValueFactory<>("kulfoldi"));
        ertekCol.setCellValueFactory(new PropertyValueFactory<>("ertek"));

        posztIdCol.setCellValueFactory(new PropertyValueFactory<>("posztNev"));

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
        String sql = "  SELECT id, mezszam,klubid,utonev, vezeteknev, szulido, magyar, kulfoldi, ertek,posztid \n" +
                "           FROM labdarugo" ;

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

                playerList.add(new Labdarugo(id, mezszam, posztid, utonev, vezeteknev, szulido, magyar,kulfoldi, ertek, klubid));
            }

            tableView.setItems(playerList);
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Hiba az adatok betöltése során: " + e.getMessage());
        }
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

        Label label = new Label("Hello GAMF!");
        root.setCenter(label);

        newWindow.setTitle("3. feladat - Ír");
        newWindow.setScene(scene);
        newWindow.show();
    }

    public void modositItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        Label label = new Label("Hello GAMF!");
        root.setCenter(label);

        newWindow.setTitle("4. feladat - Módosít");
        newWindow.setScene(scene);
        newWindow.show();
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

    public void letoltesItem(ActionEvent actionEvent) {
        Stage newWindow = new Stage();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        try {
            MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
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
}
