package Modelo;

import Modelo.Pieza;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el tablero de ajedrez y gestiona su estado.
 * Maneja la disposición de las piezas y el registro de movimientos.
 */
public class Tablero {
    /** Matriz 8x8 que representa el estado actual del tablero */
    private Pieza[][] tablero;
    /** Registro de movimientos realizados en notación PGN */
    private List<String> historialMovimientos;

    /**
     * Constructor que inicializa un nuevo tablero con la disposición
     * inicial estándar de las piezas de ajedrez.
     */
    public Tablero() {
        tablero = new Pieza[8][8];
        historialMovimientos = new ArrayList<>();
        inicializarTablero();
    }

    /**
     * Coloca todas las piezas en sus posiciones iniciales estándar.
     * Configura las piezas blancas en las filas 1 y 2, y las negras en las filas 7 y 8.
     */
    private void inicializarTablero() {
        // Inicializar piezas blancas
        tablero[0][0] = new Pieza("Torre", 'B', "a1");
        tablero[0][1] = new Pieza("Caballo", 'B', "b1");
        tablero[0][2] = new Pieza("Alfil", 'B', "c1");
        tablero[0][3] = new Pieza("Dama", 'B', "d1");
        tablero[0][4] = new Pieza("Rey", 'B', "e1");
        tablero[0][5] = new Pieza("Alfil", 'B', "f1");
        tablero[0][6] = new Pieza("Caballo", 'B', "g1");
        tablero[0][7] = new Pieza("Torre", 'B', "h1");

        for (int i = 0; i < 8; i++) {
            tablero[1][i] = new Pieza("Peón", 'B', (char)('a' + i) + "2");
        }

        // Inicializar piezas negras
        tablero[7][0] = new Pieza("Torre", 'N', "a8");
        tablero[7][1] = new Pieza("Caballo", 'N', "b8");
        tablero[7][2] = new Pieza("Alfil", 'N', "c8");
        tablero[7][3] = new Pieza("Dama", 'N', "d8");
        tablero[7][4] = new Pieza("Rey", 'N', "e8");
        tablero[7][5] = new Pieza("Alfil", 'N', "f8");
        tablero[7][6] = new Pieza("Caballo", 'N', "g8");
        tablero[7][7] = new Pieza("Torre", 'N', "h8");

        for (int i = 0; i < 8; i++) {
            tablero[6][i] = new Pieza("Peón", 'N', (char)('a' + i) + "7");
        }
    }

    /**
     * Procesa y ejecuta un movimiento en notación PGN.
     * Interpreta la notación, identifica la pieza a mover y actualiza el estado del tablero.
     * @param movimientoPGN Movimiento en notación PGN (ej: "e4", "Nf3", "exd5")
     */
    public void realizarMovimiento(String movimientoPGN) {
        if (movimientoPGN == null || movimientoPGN.isEmpty()) {
            return;
        }

        historialMovimientos.add(movimientoPGN);
        movimientoPGN = movimientoPGN.replaceAll("[+#]", "");

        try {
            char tipoPieza = 'P';
            if (Character.isUpperCase(movimientoPGN.charAt(0)) && movimientoPGN.charAt(0) != 'O') {
                tipoPieza = movimientoPGN.charAt(0);
                movimientoPGN = movimientoPGN.substring(1);
            }

            if (movimientoPGN.equals("O-O") || movimientoPGN.equals("O-O-O")) {
                return;
            }

            boolean hayCaptura = movimientoPGN.contains("x");
            if (hayCaptura) {
                movimientoPGN = movimientoPGN.replace("x", "");
            }

            String destino = movimientoPGN.substring(movimientoPGN.length() - 2);
            int columnaDestino = destino.charAt(0) - 'a';
            int filaDestino = Character.getNumericValue(destino.charAt(1)) - 1;

            String origen = movimientoPGN.substring(0, movimientoPGN.length() - 2);
            Integer filaOrigen = null;
            Integer columnaOrigen = null;

            if (origen.length() > 0) {
                for (char c : origen.toCharArray()) {
                    if (Character.isLetter(c)) {
                        columnaOrigen = c - 'a';
                    } else if (Character.isDigit(c)) {
                        filaOrigen = Character.getNumericValue(c) - 1;
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Pieza pieza = tablero[i][j];
                    if (pieza != null) {
                        boolean esLaPiezaCorrecta = false;

                        switch (tipoPieza) {
                            case 'P': esLaPiezaCorrecta = pieza.getNombre().equals("Peón"); break;
                            case 'N': esLaPiezaCorrecta = pieza.getNombre().equals("Caballo"); break;
                            case 'B': esLaPiezaCorrecta = pieza.getNombre().equals("Alfil"); break;
                            case 'R': esLaPiezaCorrecta = pieza.getNombre().equals("Torre"); break;
                            case 'Q': esLaPiezaCorrecta = pieza.getNombre().equals("Dama"); break;
                            case 'K': esLaPiezaCorrecta = pieza.getNombre().equals("Rey"); break;
                        }

                        if (esLaPiezaCorrecta &&
                                (columnaOrigen == null || columnaOrigen == j) &&
                                (filaOrigen == null || filaOrigen == i)) {

                            tablero[filaDestino][columnaDestino] = pieza;
                            tablero[i][j] = null;
                            pieza.setPosicion(String.format("%c%d", (char)('a' + columnaDestino), filaDestino + 1));
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar movimiento: " + movimientoPGN);
            e.printStackTrace();
        }
    }

    /**
     * @return Estado actual del tablero como matriz de piezas
     */
    public Pieza[][] getTablero() {
        return tablero;
    }

    /**
     * Actualiza el estado completo del tablero
     * @param tablero Nueva configuración del tablero
     */
    public void setTablero(Pieza[][] tablero) {
        this.tablero = tablero;
    }

    /**
     * @return Lista de movimientos realizados en notación PGN
     */
    public List<String> getHistorialMovimientos() {
        return historialMovimientos;
    }

    /**
     * Agrega un nuevo movimiento al historial
     * @param movimiento Movimiento en notación PGN
     */
    public void agregarMovimiento(String movimiento) {
        historialMovimientos.add(movimiento);
    }
}