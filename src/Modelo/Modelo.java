package Modelo;

import Modelo.Tablero;
import Modelo.Pieza;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del modelo que gestiona el estado del juego de ajedrez.
 * Mantiene el estado del tablero y el registro de movimientos en notación PGN.
 */
public class Modelo {
    /** Representa el tablero actual del juego */
    private Tablero tablero;
    /** Lista de movimientos en notación PGN (Portable Game Notation) */
    private List<String> movimientosPGN;

    /**
     * Constructor que inicializa un nuevo juego con tablero vacío
     * y lista de movimientos PGN.
     */
    public Modelo() {
        tablero = new Tablero();
        movimientosPGN = new ArrayList<>();
    }

    /**
     * Carga una lista de movimientos en notación PGN.
     * @param movimientos Lista de movimientos a cargar
     */
    public void cargarMovimientosPGN(List<String> movimientos) {
        this.movimientosPGN = movimientos;
    }

    /**
     * @return El tablero actual del juego
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * @return Lista de movimientos en notación PGN
     */
    public List<String> getMovimientosPGN() {
        return movimientosPGN;
    }

    /**
     * Reinicia el tablero a su estado inicial
     */
    public void reiniciarTablero() {
        tablero = new Tablero();
    }

    /**
     * Actualiza el estado del tablero con una nueva configuración de piezas
     * @param estado Nueva configuración de piezas para el tablero
     */
    public void setTablero(Pieza[][] estado) {
        tablero.setTablero(estado);
    }
}