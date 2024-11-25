package Modelo;

/**
 * Representa una pieza de ajedrez en el tablero.
 * Contiene información sobre el tipo, color y posición de la pieza.
 */
public class Pieza {
    private String nombre;
    private char color;
    private String posicion;

    /**
     * Constructor de la pieza de ajedrez.
     * @param nombre Nombre de la pieza (Rey, Dama, etc.)
     * @param color Color de la pieza ('B' para blancas, 'N' para negras)
     * @param posicion Posición en notación algebraica (e.g., "e4")
     */
    public Pieza(String nombre, char color, String posicion) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = posicion;
    }

    /**
     * Obtiene el nombre de la pieza.
     * @return Nombre de la pieza
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el color de la pieza.
     * @return Color de la pieza ('B' o 'N')
     */
    public char getColor() {
        return color;
    }

    /**
     * Obtiene la posición actual de la pieza.
     * @return Posición en notación algebraica
     */
    public String getPosicion() {
        return posicion;
    }

    /**
     * Actualiza la posición de la pieza.
     * @param posicion Nueva posición en notación algebraica
     */
    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
}