package Controlador;

import Modelo.*;
import Modelo.ReglaJuego;
import Modelo.ValidadorMovimiento;
import Modelo.Pieza;
import Modelo.TableroEstado;
import Modelo.GestorSonido;
import Vista.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


/**
 * Controlador principal del juego de ajedrez que gestiona la lógica entre el modelo y la vista.
 * Implementa ActionListener para manejar eventos de la interfaz y PropertyChangeListener
 * para la comunicación entre componentes.
 */
public class Controlador implements ActionListener, PropertyChangeListener {
    private Modelo modelo;
    private Vista vista;
    private int movimientoActual;
    private List<TableroEstado> historicoTableros;
    private boolean modoJuego;
    private Point piezaSeleccionada;
    private boolean turnoBlancas;
    private boolean jaqueMateDetectado;
    private List<String> movimientosPartida;
    private boolean partidaFinalizada;
    /**
     * Ruta del directorio donde se guardan las partidas
     */
    private static final String DIRECTORIO_PARTIDAS = "src/partidas";


    /**
     * Constructor del controlador.
     * Inicializa el controlador con el modelo y la vista del juego.
     *
     * @param modelo el modelo que contiene la lógica del juego
     * @param vista  la vista que muestra la interfaz gráfica
     */
    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.movimientoActual = 0;
        this.historicoTableros = new ArrayList<>();
        this.modoJuego = false;
        this.turnoBlancas = true;
        this.jaqueMateDetectado = false;
        this.partidaFinalizada = false;
        this.movimientosPartida = new ArrayList<>();
        vista.setControlador(this);
        vista.addPropertyChangeListener(this);
    }


    /**
     * Maneja los eventos de la interfaz de usuario.
     * Procesa acciones como cambios de modo, navegación de movimientos y guardado de partida.
     *
     * @param e el evento de acción recibido
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "MODO_VISOR":
                iniciarModoVisor();
                break;
            case "MODO_JUEGO":
                iniciarModoJuego();
                break;
            case "ANTERIOR":
                if (!modoJuego && movimientoActual > 0) {
                    movimientoActual--;
                    actualizarVista();
                    GestorSonido.reproducirSonidoMovimiento();
                }
                break;
            case "SIGUIENTE":
                if (!modoJuego && movimientoActual < historicoTableros.size() - 1) {
                    movimientoActual++;
                    actualizarVista();
                    GestorSonido.reproducirSonidoMovimiento();
                }
                break;
            case "GUARDAR_PARTIDA":
                guardarPartida();
                break;
            case "VOLVER_MENU":
                volverAlMenu();
                break;
        }
    }

    /**
     * Procesa los clicks realizados en las casillas del tablero durante una partida.
     * Este método maneja la lógica de: Selección de piezas según el turno actual
     * - Validación y ejecución de movimientos
     * - Verificación de jaque y jaque mate
     * - Cambio de turnoo
     * @param casilla Point que contiene las coordenadas (x,y) de la casilla clickeada
     *               donde x representa la fila (0-7) e y la columna (0-7)
     */
    private void manejarClickCasilla(Point casilla) {
        if (partidaFinalizada) {
            return;
        }

        Pieza[][] tablero = modelo.getTablero().getTablero();
        int fila = casilla.x;
        int columna = casilla.y;

        if (piezaSeleccionada == null) {
            Pieza pieza = tablero[fila][columna];
            if (pieza != null && ((turnoBlancas && pieza.getColor() == 'B') ||
                    (!turnoBlancas && pieza.getColor() == 'N'))) {
                piezaSeleccionada = casilla;
                vista.resaltarCasilla(fila, columna, new Color(173, 216, 230));
            }
        } else {
            int filaOrigen = piezaSeleccionada.x;
            int columnaOrigen = piezaSeleccionada.y;
            Pieza piezaOrigen = tablero[filaOrigen][columnaOrigen];

            if (ValidadorMovimiento.esMovimientoValido(piezaOrigen, filaOrigen, columnaOrigen,
                    fila, columna, modelo.getTablero())) {

                // Verificar si se está capturando al rey
                Pieza piezaDestino = tablero[fila][columna];
                boolean capturaRey = piezaDestino != null &&
                        piezaDestino.getNombre().equals("Rey");

                // Realizar el movimiento
                realizarMovimiento(filaOrigen, columnaOrigen, fila, columna);
                registrarMovimiento(filaOrigen, columnaOrigen, fila, columna, piezaOrigen);

                if (capturaRey) {
                    partidaFinalizada = true;
                    vista.detenerReloj();
                    String ganador = turnoBlancas ? "Blancas" : "Negras";
                    SwingUtilities.invokeLater(() -> {
                        finalizarPartida(true, ganador);
                    });
                    return;
                }

                // Verificar jaque mate
                Point posReyOponente = ReglaJuego.encontrarRey(!turnoBlancas, modelo.getTablero());
                if (posReyOponente != null && ReglaJuego.estaEnJaque(posReyOponente, !turnoBlancas, modelo.getTablero())) {
                    if (ReglaJuego.esJaqueMate(!turnoBlancas, modelo.getTablero())) {
                        partidaFinalizada = true;
                        jaqueMateDetectado = true;
                        vista.detenerReloj();
                        String ganador = turnoBlancas ? "Blancas" : "Negras";
                        SwingUtilities.invokeLater(() -> {
                            finalizarPartida(true, ganador);
                        });
                        return;
                    }
                }

                // Si no hay jaque mate ni captura de rey, continuar el juego
                if (!partidaFinalizada) {
                    turnoBlancas = !turnoBlancas;
                    vista.cambiarTurnoReloj();
                }
            }

            piezaSeleccionada = null;
            actualizarVista();
        }
    }

    /**
     * Registra un movimiento en notación algebraica.
     *
     * @param filaOrigen fila inicial de la pieza
     * @param columnaOrigen columna inicial de la pieza
     * @param filaDestino fila destino de la pieza
     * @param columnaDestino columna destino de la pieza
     * @param pieza la pieza que se mueve
     */
    private void registrarMovimiento(int filaOrigen, int columnaOrigen,
                                     int filaDestino, int columnaDestino, Pieza pieza) {
        if (movimientosPartida == null) {
            movimientosPartida = new ArrayList<>();
        }

        String columnas = "abcdefgh";
        String movimiento = "";

        if (!pieza.getNombre().equals("Peón")) {
            movimiento += pieza.getNombre().substring(0, 1).toUpperCase();
        }

        movimiento += columnas.charAt(columnaOrigen);
        movimiento += (8 - filaOrigen);

        Pieza[][] tablero = modelo.getTablero().getTablero();
        movimiento += (tablero[filaDestino][columnaDestino] != null) ? "x" : "-";

        movimiento += columnas.charAt(columnaDestino);
        movimiento += (8 - filaDestino);

        movimientosPartida.add(movimiento);
    }

    /**
     * Guarda el estado actual de la partida en un archivo.
     * Incluye los movimientos realizados y el resultado si la partida ha terminado.
     */
    private void guardarPartida() {
        if (movimientosPartida == null || movimientosPartida.isEmpty()) {
            vista.mostrarMensaje("No hay movimientos para guardar.");
            return;
        }

        try {
            File directorio = new File(DIRECTORIO_PARTIDAS);
            if (!directorio.exists() && !directorio.mkdirs()) {
                throw new IOException("No se pudo crear el directorio de partidas");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String nombreArchivo = "partida_" + sdf.format(new Date()) + ".txt";
            File archivo = new File(directorio, nombreArchivo);

            if (!archivo.getParentFile().exists() && !archivo.getParentFile().mkdirs()) {
                throw new IOException("No se pudo crear el directorio padre");
            }

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(archivo)))) {
                writer.println("[Fecha: " +
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "]");
                writer.println();

                for (int i = 0; i < movimientosPartida.size(); i++) {
                    if (i % 2 == 0) {
                        writer.print((i/2 + 1) + ". ");
                    }
                    writer.print(movimientosPartida.get(i) + " ");
                    if (i % 2 == 1) {
                        writer.println();
                    }
                }

                if (movimientosPartida.size() % 2 == 1) {
                    writer.println();
                }

                if (jaqueMateDetectado) {
                    writer.println(turnoBlancas ? "0-1" : "1-0");
                }
            }

            vista.mostrarMensaje("Partida guardada exitosamente en: " + archivo.getAbsolutePath());

        } catch (IOException e) {
            vista.mostrarMensaje("Error al guardar la partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicia el modo visor de partidas.
     * Carga una partida PGN y permite navegar por sus movimientos.
     */
    private void iniciarModoVisor() {
        modoJuego = false;
        vista.setModoJuego(false);
        try {
            cargarPartidaPGN("partida.pgn");
            inicializarHistoricoTableros();
            actualizarVista();
            vista.mostrarTablero();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al cargar la partida: " + ex.getMessage());
        }
    }

    /**
     * Inicia una nueva partida en modo juego.
     * Configura el tablero inicial y el reloj para ambos jugadores.
     */
    private void iniciarModoJuego() {
        reiniciarEstadoJuego();
        modoJuego = true;
        turnoBlancas = true;
        jaqueMateDetectado = false;
        partidaFinalizada = false;

        if (!vista.mostrarDialogoJugadores()) {
            return;
        }

        if (movimientosPartida != null) {
            movimientosPartida.clear();
        } else {
            movimientosPartida = new ArrayList<>();
        }

        vista.setModoJuego(true);
        modelo.reiniciarTablero();
        vista.mostrarTablero();
        vista.iniciarReloj();
        actualizarVista();
    }

    private void volverAlMenu() {
        modoJuego = false;
        vista.setModoJuego(false);
        vista.detenerReloj();
        reiniciarEstadoJuego();
        vista.mostrarMenu();
    }


    private void inicializarHistoricoTableros() {
        historicoTableros.clear();
        TableroEstado tableroInicial = new TableroEstado(modelo.getTablero().getTablero());
        historicoTableros.add(tableroInicial);

        for (String movimiento : modelo.getMovimientosPGN()) {
            if (movimiento != null && !movimiento.isEmpty()) {
                modelo.getTablero().realizarMovimiento(movimiento);
                historicoTableros.add(new TableroEstado(modelo.getTablero().getTablero()));
            }
        }

        movimientoActual = 0;
        modelo.reiniciarTablero();
    }

    /**
     * Carga y procesa un archivo PGN con los movimientos de una partida.
     *
     * @param archivo ruta del archivo PGN a cargar
     */
    private void cargarPartidaPGN(String archivo) {
        List<String> movimientos = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(archivo))) {
            StringBuilder contenidoMovimientos = new StringBuilder();

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty() && !linea.startsWith("[")) {
                    contenidoMovimientos.append(linea).append(" ");
                }
            }

            String contenido = contenidoMovimientos.toString()
                    .replaceAll("\\d+\\.", "")
                    .replaceAll("\\{.*?\\}", "")
                    .replaceAll("1-0|0-1|1/2-1/2", "")
                    .trim();

            String[] elementosMovimientos = contenido.split("\\s+");
            for (String movimiento : elementosMovimientos) {
                if (!movimiento.isEmpty()) {
                    movimientos.add(movimiento);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No se pudo encontrar el archivo PGN: " + e.getMessage());
        }

        modelo.cargarMovimientosPGN(movimientos);
    }

    private void actualizarVista() {
        if (modoJuego) {
            vista.actualizarTablero(modelo.getTablero().getTablero());
        } else if (movimientoActual >= 0 && movimientoActual < historicoTableros.size()) {
            TableroEstado estado = historicoTableros.get(movimientoActual);
            if (estado != null) {
                modelo.setTablero(estado.getEstado());
                vista.actualizarTablero(estado.getEstado());
                vista.mostrarMovimientos(modelo.getMovimientosPGN());
            }
        }
    }

    /**
     * Maneja los cambios de propiedades en los componentes.
     * Principalmente procesa los clicks en las casillas del tablero.
     *
     * @param evt el evento de cambio de propiedad
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "CLICK_CASILLA":
                if (modoJuego && !partidaFinalizada) {
                    Point casilla = (Point) evt.getNewValue();
                    manejarClickCasilla(casilla);
                }
                break;
            case "NUEVA_PARTIDA":
                iniciarModoJuego();
                break;
            case "VOLVER_MENU":
                volverAlMenu();
                break;
        }
    }

    /**
     * Procesa el movimiento de una pieza en el tablero.
     * Valida el movimiento y actualiza el estado del juego.
     *
     * @param filaOrigen fila inicial de la pieza
     * @param columnaOrigen columna inicial de la pieza
     * @param filaDestino fila destino de la pieza
     * @param columnaDestino columna destino de la pieza
     */
    private void realizarMovimiento(int filaOrigen, int columnaOrigen,
                                    int filaDestino, int columnaDestino) {
        Pieza[][] tablero = modelo.getTablero().getTablero();
        Pieza pieza = tablero[filaOrigen][columnaOrigen];

        tablero[filaDestino][columnaDestino] = pieza;
        tablero[filaOrigen][columnaOrigen] = null;
        pieza.setPosicion(String.format("%c%d", (char)('a' + columnaDestino), 8 - filaDestino));

        GestorSonido.reproducirSonidoMovimiento();
    }

    /**
     * Finaliza la partida actual.
     * Muestra el resultado y opciones para nueva partida o volver al menú.
     *
     * @param victoria true si la partida terminó en victoria, false en otro caso
     * @param equipoGanador nombre del equipo ganador
     */
    private void finalizarPartida(boolean victoria, String equipoGanador) {
        partidaFinalizada = true;
        vista.detenerReloj();
        if (victoria) {
            GestorSonido.reproducirSonidoVictoria();
            String mensajeFinal = "¡" + equipoGanador + " han ganado la partida!";
            SwingUtilities.invokeLater(() -> {
                Object[] opciones = {"Nueva Partida", "Guardar Partida", "Volver al Menú"};
                int seleccion = JOptionPane.showOptionDialog(vista,
                        mensajeFinal,
                        "Fin del Juego",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);

                if (seleccion == JOptionPane.YES_OPTION) {
                    reiniciarPartida();
                } else if (seleccion == JOptionPane.NO_OPTION) {
                    guardarPartida();
                } else {
                    volverAlMenu();
                }
            });
        }
    }

    private void reiniciarEstadoJuego() {
        partidaFinalizada = false;
        jaqueMateDetectado = false;
        turnoBlancas = true;
        piezaSeleccionada = null;
        if (movimientosPartida != null) {
            movimientosPartida.clear();
        }
        modelo.reiniciarTablero();
    }

    private void reiniciarPartida() {
        partidaFinalizada = false;
        jaqueMateDetectado = false;
        turnoBlancas = true;
        piezaSeleccionada = null;
        if (movimientosPartida != null) {
            movimientosPartida.clear();
        }
        modelo.reiniciarTablero();
        vista.iniciarReloj();
        actualizarVista();
    }


}