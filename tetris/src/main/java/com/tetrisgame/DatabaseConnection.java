package com.tetrisgame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// La configuración se lee de variables de entorno para no dejar credenciales en el código
public class DatabaseConnection {
    private static final String URL = env("TETRIS_DB_URL", "jdbc:mysql://localhost:3306/tetris");
    private static final String USER = env("TETRIS_DB_USER", "root");
    private static final String PASSWORD = env("TETRIS_DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }
}
