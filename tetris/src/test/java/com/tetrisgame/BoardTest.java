package com.tetrisgame;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static final int COLS = 10;
    private static final int ROWS = 20;

    private void fillRow(Board board, int row) {
        // Una pieza O ocupa 2x2: cinco de ellas llenan dos filas completas
        for (int x = 0; x < COLS; x += 2) {
            board.placeTetromino(new Tetromino(Tetromino.Type.O), x, row - 1);
        }
    }

    @Test
    void pieceOutsideTheBoardIsNotValid() {
        Board board = new Board(COLS, ROWS);
        Tetromino piece = new Tetromino(Tetromino.Type.O);

        assertTrue(board.isValidPosition(piece, 0, 0));
        assertFalse(board.isValidPosition(piece, -1, 0));
        assertFalse(board.isValidPosition(piece, COLS - 1, 0));
        assertFalse(board.isValidPosition(piece, 0, ROWS - 1));
    }

    @Test
    void pieceCannotOverlapPlacedBlocks() {
        Board board = new Board(COLS, ROWS);
        board.placeTetromino(new Tetromino(Tetromino.Type.O), 4, 18);

        assertFalse(board.isValidPosition(new Tetromino(Tetromino.Type.O), 4, 17));
        assertTrue(board.isValidPosition(new Tetromino(Tetromino.Type.O), 4, 16));
    }

    @Test
    void fullRowsAreClearedAndCounted() {
        Board board = new Board(COLS, ROWS);
        fillRow(board, ROWS - 1);

        assertEquals(2, board.clearLines());
        // Tras limpiar, el fondo vuelve a estar libre
        assertTrue(board.isValidPosition(new Tetromino(Tetromino.Type.O), 0, ROWS - 2));
    }

    @Test
    void incompleteRowsAreKept() {
        Board board = new Board(COLS, ROWS);
        board.placeTetromino(new Tetromino(Tetromino.Type.O), 0, ROWS - 2);

        assertEquals(0, board.clearLines());
        assertFalse(board.isValidPosition(new Tetromino(Tetromino.Type.O), 0, ROWS - 2));
    }

    @Test
    void clearAllEmptiesTheBoard() {
        Board board = new Board(COLS, ROWS);
        fillRow(board, ROWS - 1);
        board.clearAll();

        assertTrue(board.isValidPosition(new Tetromino(Tetromino.Type.O), 0, ROWS - 2));
    }

    @Test
    void fourRotationsReturnTheOriginalShape() {
        for (Tetromino.Type type : Tetromino.Type.values()) {
            Tetromino piece = new Tetromino(type);
            Point[] original = piece.copy().getBlocks();

            for (int i = 0; i < 4; i++) piece.rotateClockwise();

            assertArrayEquals(original, piece.getBlocks(), "Rotación incorrecta en " + type);
        }
    }

    @Test
    void copyDoesNotShareBlocks() {
        Tetromino piece = new Tetromino(Tetromino.Type.T);
        Tetromino copy = piece.copy();
        copy.rotateClockwise();

        assertNotEquals(piece.getBlocks()[0], copy.getBlocks()[0]);
    }
}
