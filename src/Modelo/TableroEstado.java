package Modelo;

import Modelo.Pieza;

/**
 * Representa una instantánea del estado del tablero de ajedrez.
 * Permite mantener una copia independiente del estado de las piezas
 * en un momento específico del juego.
 */
public class TableroEstado {
    /** Matriz que almacena una copia del estado del tablero */
    private Pieza[][] estado;

    /**
     * Crea una copia profunda del estado actual del tablero.
     * Cada pieza en el tablero es copiada individualmente para evitar
     * referencias compartidas.
     *
     * @param tablero Estado del tablero a copiar
     */
    public TableroEstado(Pieza[][] tablero) {
        this.estado = new Pieza[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tablero[i][j] != null) {
                    this.estado[i][j] = new Pieza(
                            tablero[i][j].getNombre(),
                            tablero[i][j].getColor(),
                            tablero[i][j].getPosicion()
                    );
                }
            }
        }
    }

    /**
     * Obtiene la copia del estado del tablero
     * @return Matriz 8x8 con el estado guardado del tablero
     */
    public Pieza[][] getEstado() {
        return estado;
    }
}