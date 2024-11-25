package Modelo;

/**
 * Representa un jugador en el juego de ajedrez.
 * Mantiene información sobre el jugador y gestiona su tiempo de juego.
 */
public class JugadorAjedrez {
    /** Nombre del jugador */
    private String nombre;
    /** Color de las piezas del jugador ('B' para blancas, 'N' para negras) */
    private char color;
    /** Tiempo restante del jugador en milisegundos */
    private int tiempoRestante;

    /**
     * Constructor que inicializa un nuevo jugador con 5 minutos de tiempo.
     * @param nombre Nombre del jugador
     * @param color Color asignado al jugador
     */
    public JugadorAjedrez(String nombre, char color) {
        this.nombre = nombre;
        this.color = color;
        this.tiempoRestante = 5 * 60 * 1000; // 5 minutos en milisegundos
    }

    /**
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return Color asignado al jugador
     */
    public char getColor() {
        return color;
    }

    /**
     * @return Tiempo restante en milisegundos
     */
    public int getTiempoRestante() {
        return tiempoRestante;
    }

    /**
     * Establece el tiempo restante del jugador
     * @param tiempoRestante Nuevo tiempo en milisegundos
     */
    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    /**
     * Reduce el tiempo restante del jugador según el tiempo transcurrido.
     * El tiempo no puede ser negativo.
     * @param milisegundos Tiempo a restar en milisegundos
     */
    public void actualizarTiempo(int milisegundos) {
        this.tiempoRestante -= milisegundos;
        if (this.tiempoRestante < 0) {
            this.tiempoRestante = 0;
        }
    }
}