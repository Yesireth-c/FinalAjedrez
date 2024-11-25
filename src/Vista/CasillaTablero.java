package Vista;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Representa una casilla del tablero de ajedrez.
 * Extiende JButton para crear casillas interactivas con colores alternados
 * típicos de un tablero de ajedrez.
 */
public class CasillaTablero extends JButton {
    private int fila;
    private int columna;
    private Color colorOriginal;

    /**
     * Constructor que crea una casilla del tablero.
     * Configura el tamaño, fuente y color según su posición.
     *
     * @param fila Posición de la fila en el tablero (0-7)
     * @param columna Posición de la columna en el tablero (0-7)
     */
    public CasillaTablero(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;

        setPreferredSize(new Dimension(60, 60));
        setFont(new Font("Arial Unicode MS", Font.PLAIN, 40));

        colorOriginal = (fila + columna) % 2 == 0 ?
                new Color(240, 217, 181) : new Color(181, 136, 99);

        setBackground(colorOriginal);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    /**
     * Restaura el color original de la casilla (beige o marrón).
     */
    public void resetColor() {
        setBackground(colorOriginal);
    }

    /**
     * @return La fila donde está ubicada la casilla
     */
    public int getFila() {
        return fila;
    }

    /**
     * @return La columna donde está ubicada la casilla
     */
    public int getColumna() {
        return columna;
    }
}