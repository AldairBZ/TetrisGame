package com.tetrisgame;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// Test de integración: solo se ejecuta si hay un MySQL con el esquema de schema.sql
class PuntuacionDAOTest {

    @Test
    void savedScoreAppearsInRankingAndMaxScore() {
        PuntuacionDAO dao = new PuntuacionDAO();
        int previousMax = dao.obtenerMaxPuntuacion();
        assumeTrue(dao.isAvailable(), "MySQL no disponible: se omite el test");

        int score = previousMax + 1;
        dao.guardarPuntuacion("TST", score);

        assertEquals(score, dao.obtenerMaxPuntuacion());
        List<String> top10 = dao.obtenerTop10();
        assertEquals("1. TST - " + score, top10.get(0));
        assertTrue(top10.size() <= 10);
    }
}
