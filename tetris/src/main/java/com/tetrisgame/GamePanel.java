package com.tetrisgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
    private final int tileSize = 30;
    private final int cols = 10;
    private final int rows = 20;
    private final Board board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int pieceX, pieceY;
    private Timer timer;
    private int level = 1;
    private int previousLevel = 1;
    private int initialDelay = 500;
    private final int maxScore = 5000;
    private Tetromino heldPiece = null;
    private boolean canHold = true;
    private final SoundPlayer soundPlayer = new SoundPlayer();
    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private final List<Tetromino.Type> bag = new ArrayList<>();
    private final Random random = new Random();
    private final PuntuacionDAO dao = new PuntuacionDAO();
    private boolean gameOver = false;

    private int score = 0;
    private int highScore = 0;

    private final int pointsPerSoftDrop = 1;
    private final int pointsPerHardDrop = 2;

    public GamePanel() {
        setPreferredSize(new Dimension(cols * tileSize + 150, rows * tileSize + 40));
        setBackground(Color.BLACK);
        setFocusable(true);

        board = new Board(cols, rows);

        highScore = dao.obtenerMaxPuntuacion();

        nextPiece = generateRandomPiece();
        spawnPiece();

        timer = new Timer(initialDelay, e -> {
            if (!tryMove(0, 1)) {
                lockPiece();
            }
            updateFloatingTexts();
            repaint();
        });

        timer.start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> tryMove(-1, 0);
                    case KeyEvent.VK_RIGHT -> tryMove(1, 0);
                    case KeyEvent.VK_DOWN -> {
                        if (tryMove(0, 1)) addScore(pointsPerSoftDrop);
                    }
                    case KeyEvent.VK_UP -> rotateWithWallKick();
                    case KeyEvent.VK_C -> holdPiece();
                    case KeyEvent.VK_SPACE -> {
                        int dropDistance = 0;
                        while (tryMove(0, 1)) dropDistance++;
                        addScore(dropDistance * pointsPerHardDrop);
                        // La pieza se fija siempre, aunque ya estuviera apoyada
                        lockPiece();
                    }
                }
                repaint();
            }
        });
    }

    private void lockPiece() {
        board.placeTetromino(currentPiece, pieceX, pieceY);
        int linesCleared = board.clearLines();
        if (linesCleared > 0) {
            int multiplier = switch (linesCleared) {
                case 2 -> 150;
                case 3 -> 175;
                case 4 -> 200;
                default -> 100;
            };
            int gained = linesCleared * multiplier;
            addScore(gained);
            floatingTexts.add(new FloatingText("+" + gained, pieceX * tileSize, pieceY * tileSize));
            soundPlayer.playEffect("/sounds/line_clear.wav");
        }
        spawnPiece();
    }

    private void addScore(int points) {
        score += points;
        if (score > highScore) highScore = score;

        int progressPercent = (score * 100) / maxScore;
        level = Math.max(1, progressPercent / 10);
        if (level != previousLevel) {
            timer.setDelay(Math.max(50, initialDelay - (level - 1) * 30));
            previousLevel = level;
        }
    }

    // Prueba a girar en el sitio y, si choca, desplazándola un poco (wall kick)
    private void rotateWithWallKick() {
        if (currentPiece.getType() == Tetromino.Type.O) return;

        Tetromino rotated = currentPiece.copy();
        rotated.rotateClockwise();
        for (int kick : new int[]{0, -1, 1, -2, 2}) {
            if (board.isValidPosition(rotated, pieceX + kick, pieceY)) {
                currentPiece = rotated;
                pieceX += kick;
                return;
            }
        }
    }

    private void updateFloatingTexts() {
        floatingTexts.removeIf(ft -> {
            ft.update();
            return !ft.isVisible();
        });
    }

    // Sistema "7-bag": salen las 7 piezas en orden aleatorio antes de repetir
    private Tetromino generateRandomPiece() {
        if (bag.isEmpty()) {
            bag.addAll(List.of(Tetromino.Type.values()));
            Collections.shuffle(bag, random);
        }
        return new Tetromino(bag.remove(bag.size() - 1));
    }

    private void spawnPiece() {
        currentPiece = nextPiece;
        nextPiece = generateRandomPiece();
        canHold = true;

        if (!findValidSpawnPosition()) {
            endGame();
        }
    }

    // Las piezas solo aparecen arriba; si no caben ahí, la partida ha terminado
    private boolean findValidSpawnPosition() {
        pieceX = cols / 2 - 1;
        pieceY = 0;
        return board.isValidPosition(currentPiece, pieceX, pieceY);
    }

    private void holdPiece() {
        if (!canHold) return;

        Tetromino temp = heldPiece;
        heldPiece = currentPiece;
        canHold = false;

        if (temp == null) {
            spawnPiece();
        } else {
            currentPiece = temp;
            if (!findValidSpawnPosition()) {
                endGame();
            }
        }
    }

    private boolean tryMove(int dx, int dy) {
        if (board.isValidPosition(currentPiece, pieceX + dx, pieceY + dy)) {
            pieceX += dx;
            pieceY += dy;
            return true;
        }
        return false;
    }
    private int getGhostPieceY() {
        int ghostY = pieceY;
        while (board.isValidPosition(currentPiece, pieceX, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, cols * tileSize, rows * tileSize);

        g.setColor(Color.WHITE);
        g.drawRect(0, 0, cols * tileSize, rows * tileSize);
        g.setColor(new Color(80, 80, 80));
        for (int i = 1; i < cols; i++) g.drawLine(i * tileSize, 0, i * tileSize, rows * tileSize);
        for (int i = 1; i < rows; i++) g.drawLine(0, i * tileSize, cols * tileSize, i * tileSize);

        board.draw(g, tileSize);

        // Dibuja bloque fantasma (ghost piece)
        int ghostY = getGhostPieceY();
        Color ghostColor = new Color(
            currentPiece.getColor().getRed(),
            currentPiece.getColor().getGreen(),
            currentPiece.getColor().getBlue(),
            80);  // transparencia ~30%
        g.setColor(ghostColor);
        for (Point p : currentPiece.getBlocks()) {
            int x = (pieceX + p.x) * tileSize;
            int y = (ghostY + p.y) * tileSize;
            g.fillRect(x, y, tileSize, tileSize);
            g.setColor(new Color(ghostColor.getRed(), ghostColor.getGreen(), ghostColor.getBlue(), 150));
            g.drawRect(x, y, tileSize, tileSize);
            g.setColor(ghostColor);
        }

        // Dibuja pieza actual
        g.setColor(currentPiece.getColor());
        for (Point p : currentPiece.getBlocks()) {
            int x = (pieceX + p.x) * tileSize;
            int y = (pieceY + p.y) * tileSize;
            g.fillRect(x, y, tileSize, tileSize);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, tileSize, tileSize);
            g.setColor(currentPiece.getColor());
        }

        // Dibuja textos flotantes
        for (FloatingText ft : floatingTexts) {
            g.setColor(new Color(255, 255, 0, ft.getAlpha()));
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(ft.getText(), ft.getX(), ft.getY());
        }

        int sideX = cols * tileSize + 10;
        int panelWidth = 140;

        // Panel siguiente pieza
        int nextY = 10;
        g.setColor(new Color(25, 25, 35));
        g.fillRoundRect(sideX, nextY, panelWidth, 140, 15, 15);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Siguiente:", sideX + 10, nextY + 20);
        drawMiniPiece(g, nextPiece, sideX, nextY + 20);

        // Panel pieza guardada
        int holdY = nextY + 150;
        g.setColor(new Color(35, 25, 25));
        g.fillRoundRect(sideX, holdY, panelWidth, 140, 15, 15);
        g.setColor(Color.PINK);
        g.drawString("Guardada:", sideX + 10, holdY + 20);
        drawMiniPiece(g, heldPiece, sideX, holdY + 20);

        // Panel puntuación y nivel
        int scoreY = holdY + 150;
        g.setColor(new Color(20, 20, 30));
        g.fillRoundRect(sideX, scoreY, panelWidth, 130, 15, 15);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.ORANGE);
        g.drawString("Puntos: " + score, sideX + 10, scoreY + 25);
        g.setColor(Color.GREEN);
        g.drawString("Récord: " + highScore, sideX + 10, scoreY + 45);
        g.setColor(Color.MAGENTA);
        g.drawString("Nivel: " + level, sideX + 10, scoreY + 65);
        int progress = (score * 100) / maxScore;
        g.setColor(Color.WHITE);
        g.drawString("Progreso: " + progress + "%", sideX + 10, scoreY + 85);

        // Sección: Controles
        int controlsY = scoreY + 140;
        g.setColor(new Color(15, 15, 20));
        g.fillRoundRect(sideX, controlsY, panelWidth, 120, 15, 15);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("Controles:", sideX + 10, controlsY + 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("←/→: Mover", sideX + 10, controlsY + 40);
        g.drawString("↓: Bajar suave", sideX + 10, controlsY + 55);
        g.drawString("↑: Girar", sideX + 10, controlsY + 70);
        g.drawString("Espacio: Caída", sideX + 10, controlsY + 85);
        g.drawString("C: Guardar", sideX + 10, controlsY + 100);
    }


    private void drawMiniPiece(Graphics g, Tetromino piece, int areaX, int areaY) {
        if (piece == null) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : piece.getBlocks()) {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;

        int offsetX = (140 - sizeX * 20) / 2;
        int offsetY = (120 - sizeY * 20) / 2 + areaY;

        g.setColor(piece.getColor());
        for (Point p : piece.getBlocks()) {
            int x = areaX + offsetX + (p.x - minX) * 20;
            int y = offsetY + (p.y - minY) * 20;
            g.fillRect(x, y, 20, 20);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, 20, 20);
            g.setColor(piece.getColor());
        }
    }

    private void endGame() {
        if (gameOver) return;
        gameOver = true;
        timer.stop();

        // Cancelar el diálogo ya no deja al jugador atrapado: simplemente no se guarda
        String nombre = JOptionPane.showInputDialog(this, "Nombre (3 caracteres):");
        while (nombre != null && nombre.trim().length() != 3) {
            nombre = JOptionPane.showInputDialog(this, "Nombre inválido. (3 caracteres):");
        }
        if (nombre != null) {
            dao.guardarPuntuacion(nombre.trim().toUpperCase(), score);
        }

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Game Over\nPuntos: ").append(score)
               .append("\nRécord: ").append(highScore)
               .append("\n\nTOP 10:\n");

        List<String> top10 = dao.obtenerTop10();
        if (top10.isEmpty()) mensaje.append("(ranking no disponible sin MySQL)\n");
        for (String entrada : top10) mensaje.append(entrada).append("\n");

        JOptionPane.showMessageDialog(this, mensaje.toString(), "Tetris", JOptionPane.INFORMATION_MESSAGE);

        int opcion = JOptionPane.showConfirmDialog(this, "¿Quieres jugar otra vez?", "Reiniciar juego", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        // Reinicia variables de juego
        score = 0;
        level = 1;
        previousLevel = 1;
        canHold = true;
        heldPiece = null;
        gameOver = false;
        floatingTexts.clear();
        board.clearAll();
        bag.clear();
        nextPiece = generateRandomPiece();
        spawnPiece();

        int newDelay = initialDelay;
        timer.setDelay(newDelay);
        timer.start();

        repaint();
    }


    private static class FloatingText {
        private final String text;
        private int x, y;
        private int alpha = 255;
        private final int fadeSpeed = 5;
        private final int riseSpeed = 1;

        public FloatingText(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        public void update() {
            y -= riseSpeed;
            alpha -= fadeSpeed;
            if (alpha < 0) alpha = 0;
        }

        public boolean isVisible() {
            return alpha > 0;
        }

        public int getAlpha() {
            return alpha;
        }

        public String getText() {
            return text;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
