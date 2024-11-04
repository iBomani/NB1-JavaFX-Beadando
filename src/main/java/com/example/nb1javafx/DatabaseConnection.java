package com.example.nb1javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DatabaseConnection {
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:../adatok.db";
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
