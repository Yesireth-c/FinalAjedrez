package Modelo;

import Modelo.Pieza;
import Modelo.Tablero;
import java.awt.Point;

/**
 * Clase que valida los movimientos de las piezas en un juego de ajedrez.
 * Contiene métodos para verificar si los movimientos de cada tipo de pieza son válidos
 * según las reglas del ajedrez.
 */
public class ValidadorMovimiento {
    /**
     * Valida si un movimiento es legal para una pieza específica.
     *
     * @param pieza La pieza que se desea mover
     * @param filaOrigen Fila inicial de la pieza
     * @param columnaOrigen Columna inicial de la pieza
     * @param filaDestino Fila destino del movimiento
     * @param columnaDestino Columna destino del movimiento
     * @param tablero Estado actual del tablero
     * @return true si el movimiento es válido, false en caso contrario
     */
    public static boolean esMovimientoValido(Pieza pieza, int filaOrigen, int columnaOrigen,
                                             int filaDestino, int columnaDestino, Tablero tablero) {
        Pieza[][] estadoTablero = tablero.getTablero();

        // Validar que no se capture una pieza del mismo color
        if (estadoTablero[filaDestino][columnaDestino] != null &&
                estadoTablero[filaDestino][columnaDestino].getColor() == pieza.getColor()) {
            return false;
        }

        switch (pieza.getNombre()) {
            case "Peón":
                return validarMovimientoPeon(pieza, filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino, estadoTablero);
            case "Torre":
                return validarMovimientoTorre(filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino, estadoTablero);
            case "Alfil":
                return validarMovimientoAlfil(filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino, estadoTablero);
            case "Caballo":
                return validarMovimientoCaballo(filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino);
            case "Dama":
                return validarMovimientoTorre(filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino, estadoTablero) ||
                        validarMovimientoAlfil(filaOrigen, columnaOrigen,
                                filaDestino, columnaDestino, estadoTablero);
            case "Rey":
                return validarMovimientoRey(filaOrigen, columnaOrigen,
                        filaDestino, columnaDestino);
            default:
                return false;
        }
    }

    /**
     * Valida el movimiento de un peón.
     * Incluye movimiento simple hacia adelante, movimiento doble inicial y capturas diagonales.
     */
    private static boolean validarMovimientoPeon(Pieza peon, int filaOrigen, int columnaOrigen,
                                                 int filaDestino, int columnaDestino, Pieza[][] tablero) {
        int direccion = peon.getColor() == 'B' ? 1 : -1;

        // Movimiento simple hacia adelante
        if (columnaOrigen == columnaDestino && filaDestino == filaOrigen + direccion &&
                tablero[filaDestino][columnaDestino] == null) {
            return true;
        }

        // Movimiento inicial doble
        if (columnaOrigen == columnaDestino &&
                ((peon.getColor() == 'B' && filaOrigen == 1 && filaDestino == 3) ||
                        (peon.getColor() == 'N' && filaOrigen == 6 && filaDestino == 4)) &&
                tablero[filaOrigen + direccion][columnaDestino] == null &&
                tablero[filaDestino][columnaDestino] == null) {
            return true;
        }

        // Captura diagonal
        if (Math.abs(columnaDestino - columnaOrigen) == 1 &&
                filaDestino == filaOrigen + direccion &&
                tablero[filaDestino][columnaDestino] != null &&
                tablero[filaDestino][columnaDestino].getColor() != peon.getColor()) {
            return true;
        }

        return false;
    }

    /**
     * Valida el movimiento de una torre.
     * Verifica movimientos horizontales y verticales, asegurando que no haya piezas en el camino.
     */
    private static boolean validarMovimientoTorre(int filaOrigen, int columnaOrigen,
                                                  int filaDestino, int columnaDestino,
                                                  Pieza[][] tablero) {
        if (filaOrigen != filaDestino && columnaOrigen != columnaDestino) {
            return false;
        }

        if (filaOrigen == filaDestino) {
            int inicio = Math.min(columnaOrigen, columnaDestino);
            int fin = Math.max(columnaOrigen, columnaDestino);
            for (int col = inicio + 1; col < fin; col++) {
                if (tablero[filaOrigen][col] != null) {
                    return false;
                }
            }
        } else {
            int inicio = Math.min(filaOrigen, filaDestino);
            int fin = Math.max(filaOrigen, filaDestino);
            for (int fila = inicio + 1; fila < fin; fila++) {
                if (tablero[fila][columnaOrigen] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Valida el movimiento de un alfil.
     * Verifica movimientos diagonales, asegurando que no haya piezas en el camino.
     */
    private static boolean validarMovimientoAlfil(int filaOrigen, int columnaOrigen,
                                                  int filaDestino, int columnaDestino,
                                                  Pieza[][] tablero) {
        if (Math.abs(filaDestino - filaOrigen) != Math.abs(columnaDestino - columnaOrigen)) {
            return false;
        }

        int filaDir = filaDestino > filaOrigen ? 1 : -1;
        int colDir = columnaDestino > columnaOrigen ? 1 : -1;
        int fila = filaOrigen + filaDir;
        int col = columnaOrigen + colDir;

        while (fila != filaDestino && col != columnaDestino) {
            if (tablero[fila][col] != null) {
                return false;
            }
            fila += filaDir;
            col += colDir;
        }
        return true;
    }

    /**
     * Valida el movimiento de un caballo.
     * Verifica el patrón en L característico del caballo.
     */
    private static boolean validarMovimientoCaballo(int filaOrigen, int columnaOrigen,
                                                    int filaDestino, int columnaDestino) {
        int difFila = Math.abs(filaDestino - filaOrigen);
        int difCol = Math.abs(columnaDestino - columnaOrigen);
        return (difFila == 2 && difCol == 1) || (difFila == 1 && difCol == 2);
    }

    /**
     * Valida el movimiento de un rey.
     * Verifica que el movimiento sea de una casilla en cualquier dirección.
     */
    private static boolean validarMovimientoRey(int filaOrigen, int columnaOrigen,
                                                int filaDestino, int columnaDestino) {
        return Math.abs(filaDestino - filaOrigen) <= 1 &&
                Math.abs(columnaDestino - columnaOrigen) <= 1;
    }
}