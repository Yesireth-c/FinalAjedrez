package Modelo;

import Modelo.Pieza;
import Modelo.Tablero;
import java.awt.Point;

/**
 * Gestiona las reglas principales del juego de ajedrez, incluyendo la detección
 * de jaque y jaque mate.
 */
public class ReglaJuego {
    /**
     * Verifica si un rey está en jaque.
     * @param posRey Posición del rey en el tablero
     * @param esReyBlanco true si es el rey blanco, false si es el negro
     * @param tablero Estado actual del tablero
     * @return true si el rey está en jaque, false en caso contrario
     */
    public static boolean estaEnJaque(Point posRey, boolean esReyBlanco, Tablero tablero) {
        if (posRey == null) return false;

        Pieza[][] estadoTablero = tablero.getTablero();
        char colorOponente = esReyBlanco ? 'N' : 'B';

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza pieza = estadoTablero[i][j];
                if (pieza != null && pieza.getColor() == colorOponente) {
                    if (ValidadorMovimiento.esMovimientoValido(pieza, i, j, posRey.x, posRey.y, tablero)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verifica si un jugador está en jaque mate.
     * Comprueba todos los movimientos posibles para determinar si hay alguna
     * forma de escapar del jaque.
     * @param esReyBlanco true si se verifica el rey blanco, false para el negro
     * @param tablero Estado actual del tablero
     * @return true si es jaque mate, false en caso contrario
     */
    public static boolean esJaqueMate(boolean esReyBlanco, Tablero tablero) {
        Point posRey = encontrarRey(esReyBlanco, tablero);
        if (posRey == null || !estaEnJaque(posRey, esReyBlanco, tablero)) {
            return false;
        }

        Pieza[][] estadoTablero = tablero.getTablero();
        char colorRey = esReyBlanco ? 'B' : 'N';
        Tablero tableroTemporal = new Tablero();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza pieza = estadoTablero[i][j];
                if (pieza != null && pieza.getColor() == colorRey) {
                    for (int destFila = 0; destFila < 8; destFila++) {
                        for (int destCol = 0; destCol < 8; destCol++) {
                            if (ValidadorMovimiento.esMovimientoValido(pieza, i, j, destFila, destCol, tablero)) {
                                copiarTablero(estadoTablero, tableroTemporal.getTablero());
                                tableroTemporal.getTablero()[destFila][destCol] = tableroTemporal.getTablero()[i][j];
                                tableroTemporal.getTablero()[i][j] = null;

                                Point posReyTemporal = pieza.getNombre().equals("Rey") ?
                                        new Point(destFila, destCol) : new Point(posRey.x, posRey.y);

                                if (!estaEnJaque(posReyTemporal, esReyBlanco, tableroTemporal)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Copia el estado de un tablero a otro.
     * @param origen Tablero original
     * @param destino Tablero donde se copiará el estado
     */
    private static void copiarTablero(Pieza[][] origen, Pieza[][] destino) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (origen[i][j] != null) {
                    destino[i][j] = new Pieza(
                            origen[i][j].getNombre(),
                            origen[i][j].getColor(),
                            origen[i][j].getPosicion()
                    );
                } else {
                    destino[i][j] = null;
                }
            }
        }
    }

    /**
     * Encuentra la posición del rey del color especificado en el tablero.
     * @param esReyBlanco true para buscar el rey blanco, false para el negro
     * @param tablero Estado actual del tablero
     * @return Point con la posición del rey, o null si no se encuentra
     */
    public static Point encontrarRey(boolean esReyBlanco, Tablero tablero) {
        Pieza[][] estadoTablero = tablero.getTablero();
        char colorRey = esReyBlanco ? 'B' : 'N';

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza pieza = estadoTablero[i][j];
                if (pieza != null && pieza.getNombre().equals("Rey") &&
                        pieza.getColor() == colorRey) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }
}