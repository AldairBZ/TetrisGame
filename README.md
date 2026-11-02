# TetrisGame

Tetris de escritorio hecho en **Java con Swing**, con ranking de puntuaciones guardado en **MySQL** mediante JDBC.

Lo hice para practicar programación orientada a objetos aplicada a algo con reglas claras y estado que cambia todo el rato: un tablero, piezas que giran, colisiones, puntuación y niveles.

![Java](https://img.shields.io/badge/Java-21-orange) ![Swing](https://img.shields.io/badge/UI-Swing-blue) ![MySQL](https://img.shields.io/badge/DB-MySQL-4479A1) ![JUnit](https://img.shields.io/badge/tests-JUnit%205-25A162)

## Qué incluye

- Las 7 piezas clásicas, con **generador "7-bag"**: salen las siete en orden aleatorio antes de repetir, así no hay rachas injustas.
- **Pieza fantasma**, que muestra dónde va a caer la pieza.
- **Guardar pieza** (tecla `C`) y vista previa de la siguiente.
- **Giro con wall kick**: si la pieza choca contra la pared al girar, se desplaza para poder girar.
- **Puntuación y niveles**: más líneas a la vez dan más puntos, y la velocidad sube con el nivel.
- Música, efectos de sonido y textos flotantes al hacer líneas.
- **Ranking Top 10 en MySQL**. Si no hay base de datos, el juego funciona igual, sin ranking.

## Controles

| Tecla | Acción |
|---|---|
| `←` `→` | Mover |
| `↓` | Bajar (suma 1 punto por casilla) |
| `↑` | Girar |
| `Espacio` | Caída instantánea (suma 2 puntos por casilla) |
| `C` | Guardar pieza |

## Cómo ejecutarlo

Necesitas **Java 21** o superior y **Maven**.

```bash
cd tetris
mvn compile exec:java
```

### Ranking con MySQL (opcional)

1. Crea la base de datos con el script incluido:
   ```bash
   mysql -u root -p < src/main/resources/schema.sql
   ```
2. Si tu usuario o contraseña no son los de por defecto (`root`, sin contraseña), indícalos con variables de entorno:
   ```bash
   TETRIS_DB_URL=jdbc:mysql://localhost:3306/tetris
   TETRIS_DB_USER=tu_usuario
   TETRIS_DB_PASSWORD=tu_contraseña
   ```

Las credenciales no están en el código: se leen del entorno.

## Tests

```bash
cd tetris
mvn test
```

Los tests (`BoardTest`) cubren la lógica del juego que no depende de la interfaz: límites del tablero, colisiones, limpieza de líneas y giros de las piezas.

## Estructura

```
tetris/src/main/java/com/tetrisgame/
├── Main.java               # Arranca la ventana y la música
├── GamePanel.java          # Bucle de juego, controles, puntuación y dibujo
├── Board.java              # Tablero: colisiones y limpieza de líneas
├── Tetromino.java          # Piezas: formas, colores y giros
├── PuntuacionDAO.java      # Acceso al ranking (JDBC)
├── DatabaseConnection.java # Configuración de la conexión
└── SoundPlayer.java        # Música y efectos
```

## Qué aprendí

- A separar la **lógica del juego** (`Board`, `Tetromino`) de la **interfaz** (`GamePanel`). Gracias a eso, esa lógica se puede testear sin abrir ninguna ventana.
- A usar el patrón **DAO** para que el juego no dependa de cómo se guardan las puntuaciones.
- A trabajar con el bucle de eventos de Swing (`Timer`, `KeyListener`, `paintComponent`).

## Próximos pasos

- Persistencia del ranking en local (fichero) cuando no hay MySQL.
- Pantalla de pausa.

---

Hecho por [Scheneider Baltazar](https://www.linkedin.com/in/scheneiderbz19/).
