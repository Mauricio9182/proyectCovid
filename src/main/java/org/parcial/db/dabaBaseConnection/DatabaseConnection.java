package org.parcial.db.dabaBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {

        String url = "jdbc:mysql://127.0.0.1:3306/proyecto_covid";
        String user = "root";
        String password = "Soynoob9182";

        return DriverManager.getConnection(url, user, password);
    }
}
