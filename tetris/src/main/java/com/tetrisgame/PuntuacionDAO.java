package com.tetrisgame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuntuacionDAO {
    // Si la base de datos no responde, el juego sigue funcionando sin ranking
    private boolean available = true;

    public void guardarPuntuacion(String jugador, int puntuacion) {
        if (!available) return;
        String sql = "INSERT INTO puntuaciones (jugador, puntuacion) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jugador);
            stmt.setInt(2, puntuacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            disable(e);
        }
    }

    public int obtenerMaxPuntuacion() {
        if (!available) return 0;
        String sql = "SELECT MAX(puntuacion) AS max_puntuacion FROM puntuaciones";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_puntuacion");
            }
        } catch (SQLException e) {
            disable(e);
        }
        return 0;
    }

    public List<String> obtenerTop10() {
        List<String> top10 = new ArrayList<>();
        if (!available) return top10;
        String sql = "SELECT jugador, puntuacion FROM puntuaciones ORDER BY puntuacion DESC LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int posicion = 1;
            while (rs.next()) {
                top10.add(posicion + ". " + rs.getString("jugador") + " - " + rs.getInt("puntuacion"));
                posicion++;
            }
        } catch (SQLException e) {
            disable(e);
        }
        return top10;
    }

    public boolean isAvailable() {
        return available;
    }

    private void disable(SQLException e) {
        available = false;
        System.err.println("Ranking desactivado (no hay conexión con MySQL): " + e.getMessage());
    }
}
