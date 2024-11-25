package Vista;

import Modelo.GestorSonido;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Componente visual que implementa un reloj de ajedrez.
 * Controla el tiempo de cada jugador y muestra una cuenta regresiva
 * con un tiempo inicial de 5 minutos por jugador.
 */
public class RelojAjedrez extends JPanel {
    private JLabel lblTiempoBlancas;
    private JLabel lblTiempoNegras;
    private JLabel lblNombreBlancas;
    private JLabel lblNombreNegras;
    private Timer timer;
    private boolean turnoBlancas;
    private int tiempoBlancas;
    private int tiempoNegras;
    private static final int TIEMPO_INICIAL = 300; // 5 minutos en segundos
    private boolean relojActivo;

    /**
     * Constructor del reloj de ajedrez.
     * Inicializa el panel con un layout de cuadrícula y configura
     * el tiempo inicial para ambos jugadores.
     */
    public RelojAjedrez() {
        setLayout(new GridLayout(2, 2, 10, 5));
        inicializarComponentes();
        reiniciarTiempos();
    }

    /**
     * Inicializa y configura los componentes visuales del reloj.
     * Establece las etiquetas, fuentes y estilos para mostrar
     * los nombres de los jugadores y sus tiempos.
     */
    private void inicializarComponentes() {
        lblNombreBlancas = new JLabel("Blancas", SwingConstants.CENTER);
        lblNombreNegras = new JLabel("Negras", SwingConstants.CENTER);
        lblTiempoBlancas = new JLabel("5:00", SwingConstants.CENTER);
        lblTiempoNegras = new JLabel("5:00", SwingConstants.CENTER);

        Font fuenteNombres = new Font("Arial", Font.BOLD, 14);
        Font fuenteTiempo = new Font("Monospaced", Font.BOLD, 20);

        lblNombreBlancas.setFont(fuenteNombres);
        lblNombreNegras.setFont(fuenteNombres);
        lblTiempoBlancas.setFont(fuenteTiempo);
        lblTiempoNegras.setFont(fuenteTiempo);

        add(lblNombreBlancas);
        add(lblTiempoBlancas);
        add(lblNombreNegras);
        add(lblTiempoNegras);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Reinicia los tiempos a sus valores iniciales
     */
    private void reiniciarTiempos() {
        tiempoBlancas = TIEMPO_INICIAL;
        tiempoNegras = TIEMPO_INICIAL;
        actualizarEtiquetaTiempo(lblTiempoBlancas, tiempoBlancas);
        actualizarEtiquetaTiempo(lblTiempoNegras, tiempoNegras);
    }

    /**
     * Inicia el reloj para una nueva partida.
     * Configura el timer y establece el turno inicial para las blancas.
     */
    public void iniciarReloj() {
        detenerReloj();
        reiniciarTiempos();
        turnoBlancas = true;
        relojActivo = true;

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(1000, e -> {
            if (relojActivo) {
                actualizarTiempo();
            }
        });
        timer.start();
        actualizarEstilos();
    }

    /**
     * Detiene el reloj de la partida actual.
     */
    public void detenerReloj() {
        relojActivo = false;
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Cambia el turno entre jugadores y actualiza los estilos visuales.
     */
    public void cambiarTurno() {
        if (!relojActivo) return;

        turnoBlancas = !turnoBlancas;

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(1000, e -> {
            if (relojActivo) {
                actualizarTiempo();
            }
        });
        timer.start();

        actualizarEstilos();
    }

    /**
     * Actualiza el tiempo del jugador actual y verifica si se ha agotado.
     */
    private void actualizarTiempo() {
        if (!relojActivo) return;

        if (turnoBlancas && tiempoBlancas > 0) {
            tiempoBlancas--;
            actualizarEtiquetaTiempo(lblTiempoBlancas, tiempoBlancas);
            if (tiempoBlancas <= 0) {
                tiempoAgotado("Negras");
            }
        } else if (!turnoBlancas && tiempoNegras > 0) {
            tiempoNegras--;
            actualizarEtiquetaTiempo(lblTiempoNegras, tiempoNegras);
            if (tiempoNegras <= 0) {
                tiempoAgotado("Blancas");
            }
        }
    }

    /**
     * Maneja el evento de tiempo agotado para un jugador.
     *
     * @param ganador El color del jugador ganador
     */
    private void tiempoAgotado(String ganador) {
        detenerReloj();
        GestorSonido.reproducirSonidoVictoria();
        firePropertyChange("TIEMPO_AGOTADO", null, ganador);
    }

    /**
     * Actualiza la etiqueta de tiempo con el formato mm:ss.
     *
     * @param label Etiqueta a actualizar
     * @param segundosTotales Tiempo en segundos a mostrar
     */
    private void actualizarEtiquetaTiempo(JLabel label, int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        DecimalFormat df = new DecimalFormat("00");
        label.setText(minutos + ":" + df.format(segundos));
    }

    /**
     * Actualiza los estilos visuales según el turno actual.
     */
    private void actualizarEstilos() {
        Color colorActivo = new Color(46, 204, 113);
        Color colorInactivo = new Color(200, 200, 200);

        lblNombreBlancas.setForeground(turnoBlancas ? colorActivo : colorInactivo);
        lblTiempoBlancas.setForeground(turnoBlancas ? colorActivo : colorInactivo);
        lblNombreNegras.setForeground(turnoBlancas ? colorInactivo : colorActivo);
        lblTiempoNegras.setForeground(turnoBlancas ? colorInactivo : colorActivo);
    }

    /**
     * Establece los nombres de los jugadores en el reloj.
     *
     * @param nombreBlancas Nombre del jugador con piezas blancas
     * @param nombreNegras Nombre del jugador con piezas negras
     */
    public void setNombres(String nombreBlancas, String nombreNegras) {
        lblNombreBlancas.setText(nombreBlancas);
        lblNombreNegras.setText(nombreNegras);
    }

    /**
     * Muestra un diálogo cuando finaliza la partida.
     * Permite elegir entre iniciar una nueva partida o volver al menú.
     *
     * @param mensaje Mensaje a mostrar en el diálogo
     */
    private void mostrarDialogoFinPartida(String mensaje) {
        Object[] opciones = {"Nueva Partida", "Volver al Menú"};
        int seleccion = JOptionPane.showOptionDialog(this,
                mensaje,
                "Fin del Juego",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion == JOptionPane.YES_OPTION) {
            firePropertyChange("NUEVA_PARTIDA", null, null);
        } else {
            firePropertyChange("VOLVER_MENU", null, null);
        }
    }
}