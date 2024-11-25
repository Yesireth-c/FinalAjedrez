package Vista;

import Modelo.GestorSonido;
import Vista.CasillaTablero;
import Modelo.Pieza;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Clase principal que maneja la interfaz gráfica del juego de ajedrez.
 * Implementa tanto el modo de juego como el modo visor de partidas.
 */
public class Vista extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(28, 27, 34);
    private static final Color BUTTON_COLOR = new Color(103, 58, 183);
    private static final Color BUTTON_HOVER_COLOR = new Color(126, 87, 194);
    private static final Color ACCENT_COLOR = new Color(255, 215, 0);

    private static final Font TITLE_FONT = new Font("Palatino", Font.BOLD, 72);
    private static final Font SUBTITLE_FONT = new Font("Palatino", Font.ITALIC, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);


    private JPanel panelTablero;
    private JPanel panelMenu;
    private CasillaTablero[][] casillas;
    private JButton btnAnterior, btnSiguiente, btnVolverMenu, btnGuardarPartida;
    private JButton btnModoVisor, btnModoJuego;
    private JTextArea areaMovimientos;
    private RelojAjedrez relojAjedrez;
    private static final Map<String, String> SIMBOLOS_UNICODE = new HashMap<>();
    private boolean modoJuego = false;


    /**
     * Mapa que contiene los símbolos Unicode de las piezas de ajedrez.
     * Asocia el nombre y color de la pieza con su símbolo correspondiente.
     */
    static {
        SIMBOLOS_UNICODE.put("Rey-B", "♔");
        SIMBOLOS_UNICODE.put("Dama-B", "♕");
        SIMBOLOS_UNICODE.put("Torre-B", "♖");
        SIMBOLOS_UNICODE.put("Alfil-B", "♗");
        SIMBOLOS_UNICODE.put("Caballo-B", "♘");
        SIMBOLOS_UNICODE.put("Peón-B", "♙");
        SIMBOLOS_UNICODE.put("Rey-N", "♚");
        SIMBOLOS_UNICODE.put("Dama-N", "♛");
        SIMBOLOS_UNICODE.put("Torre-N", "♜");
        SIMBOLOS_UNICODE.put("Alfil-N", "♝");
        SIMBOLOS_UNICODE.put("Caballo-N", "♞");
        SIMBOLOS_UNICODE.put("Peón-N", "♟");
    }


    /**
     * Construye una nueva ventana de ajedrez.
     * Inicializa todos los componentes y muestra el menú principal.
     */
    public Vista() {
        configurarVentana();
        inicializarComponentes();
        mostrarMenu();
    }

    private void configurarVentana() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void inicializarComponentes() {
        inicializarPanelMenu();
        inicializarTablero();
        inicializarBotonesControl();
        inicializarAreaMovimientos();
        inicializarReloj();
    }

    private void inicializarReloj() {
        relojAjedrez = new RelojAjedrez();
        relojAjedrez.setPreferredSize(new Dimension(200, 200));
        relojAjedrez.setBackground(new Color(45, 45, 45));
    }

    public boolean mostrarDialogoJugadores() {
        DialogoJugadores dialogo = new DialogoJugadores(this);
        dialogo.setVisible(true);

        if (dialogo.isAceptado()) {
            String jugadorBlancas = dialogo.getNombreBlancas();
            String jugadorNegras = dialogo.getNombreNegras();
            relojAjedrez.setNombres(jugadorBlancas, jugadorNegras);
            return true;
        }
        return false;
    }


    private void inicializarPanelMenu() {
        panelMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente de fondo más suave
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(28, 27, 34),
                        0, getHeight(), new Color(45, 41, 58)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Patrón decorativo
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getHeight(); i += 20) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));

        // Panel para el título con efecto de sombra
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("La Magia del Ajedrez");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT_COLOR);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Experiencia de Juego de Reyes");
        subtitulo.setFont(SUBTITLE_FONT);
        subtitulo.setForeground(new Color(200, 200, 200));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(titulo);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitulo);

        btnModoVisor = crearBotonMenu("Visor de Partidas");
        btnModoJuego = crearBotonMenu("Modo Juego");

        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(titlePanel);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 80)));
        panelMenu.add(btnModoVisor);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 30)));
        panelMenu.add(btnModoJuego);
        panelMenu.add(Box.createVerticalGlue());
    }

    private void inicializarTablero() {
        panelTablero = new JPanel(new GridLayout(8, 8));
        casillas = new CasillaTablero[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                casillas[i][j] = new CasillaTablero(i, j);
                panelTablero.add(casillas[i][j]);
            }
        }
    }

    private void inicializarBotonesControl() {
        btnAnterior = new JButton("Anterior");
        btnSiguiente = new JButton("Siguiente");
        btnVolverMenu = new JButton("Volver al Menú");
        btnGuardarPartida = new JButton("Guardar Partida");
        estilizarBotonesControl();
    }

    private void inicializarAreaMovimientos() {
        areaMovimientos = new JTextArea(10, 30);
        areaMovimientos.setEditable(false);
        areaMovimientos.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaMovimientos.setBackground(new Color(245, 245, 245));
    }

    private JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Efecto de gradiente en el botón
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, BUTTON_HOVER_COLOR.darker(),
                            0, getHeight(), BUTTON_HOVER_COLOR);
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, BUTTON_HOVER_COLOR,
                            0, getHeight(), BUTTON_HOVER_COLOR.brighter());
                } else {
                    gp = new GradientPaint(0, 0, BUTTON_COLOR,
                            0, getHeight(), BUTTON_COLOR.brighter());
                }

                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Efecto de brillo en el borde
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                // Texto con sombra
                g2d.setFont(BUTTON_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(getText(), x + 1, y + 1);
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);
            }
        };

        boton.setFont(BUTTON_FONT);
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(300, 70));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    private void estilizarBotonesControl() {
        Component[] botones = {btnAnterior, btnSiguiente, btnVolverMenu, btnGuardarPartida};
        for (Component comp : botones) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setFont(BUTTON_FONT);
                btn.setBackground(BUTTON_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BUTTON_COLOR.darker(), 2),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(BUTTON_HOVER_COLOR);
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(BUTTON_COLOR);
                    }
                });
            }
        }
    }




    public void mostrarTablero() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(BACKGROUND_COLOR);
        panelPrincipal.add(panelTablero, BorderLayout.CENTER);

        if (modoJuego) {
            panelPrincipal.add(relojAjedrez, BorderLayout.EAST);
        }

        add(panelPrincipal, BorderLayout.CENTER);

        JPanel panelControles = new JPanel();
        panelControles.setBackground(BACKGROUND_COLOR);
        if (!modoJuego) {
            panelControles.add(btnAnterior);
            panelControles.add(btnSiguiente);
        } else {
            panelControles.add(btnGuardarPartida);
        }
        panelControles.add(btnVolverMenu);
        add(panelControles, BorderLayout.SOUTH);

        if (!modoJuego) {
            JScrollPane scrollMovimientos = new JScrollPane(areaMovimientos);
            scrollMovimientos.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE),
                    "Movimientos",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    BUTTON_FONT,
                    Color.WHITE
            ));
            scrollMovimientos.setBackground(BACKGROUND_COLOR);
            areaMovimientos.setBackground(new Color(45, 45, 112));
            areaMovimientos.setForeground(Color.WHITE);
            add(scrollMovimientos, BorderLayout.EAST);
        }

        revalidate();
        repaint();
    }

    /**
     * Actualiza la visualización del tablero con el estado actual de las piezas.
     * @param estadoTablero matriz que representa la posición actual de las piezas
     */

    public void actualizarTablero(Pieza[][] estadoTablero) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (estadoTablero[i][j] != null) {
                    Pieza pieza = estadoTablero[i][j];
                    String clave = pieza.getNombre() + "-" + pieza.getColor();
                    String simbolo = SIMBOLOS_UNICODE.get(clave);

                    casillas[i][j].setText(simbolo != null ? simbolo :
                            pieza.getNombre().substring(0, 1));
                    casillas[i][j].setForeground(pieza.getColor() == 'B' ?
                            new Color(255, 250, 240) : Color.BLACK);
                } else {
                    casillas[i][j].setText("");
                }
                casillas[i][j].resetColor();
            }
        }
    }


    /**
     * Configura el controlador para manejar los eventos de la interfaz.
     * @param controlador ActionListener que procesará los eventos
     */
    public void setControlador(ActionListener controlador) {
        btnModoVisor.addActionListener(controlador);
        btnModoVisor.setActionCommand("MODO_VISOR");
        btnModoJuego.addActionListener(controlador);
        btnModoJuego.setActionCommand("MODO_JUEGO");
        btnAnterior.addActionListener(controlador);
        btnAnterior.setActionCommand("ANTERIOR");
        btnSiguiente.addActionListener(controlador);
        btnSiguiente.setActionCommand("SIGUIENTE");
        btnVolverMenu.addActionListener(controlador);
        btnVolverMenu.setActionCommand("VOLVER_MENU");
        btnGuardarPartida.addActionListener(controlador);
        btnGuardarPartida.setActionCommand("GUARDAR_PARTIDA");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int fila = i;
                final int columna = j;
                casillas[i][j].addActionListener(e -> {
                    if (modoJuego) {
                        firePropertyChange("CLICK_CASILLA", null, new Point(fila, columna));
                    }
                });
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        if (relojAjedrez != null) {
            relojAjedrez.addPropertyChangeListener(listener);
        }
    }



    /**
     * Resalta una casilla específica del tablero con un color determinado.
     * @param fila fila de la casilla (0-7)
     * @param columna columna de la casilla (0-7)
     * @param color color con el que se resaltará la casilla
     */
    public void resaltarCasilla(int fila, int columna, Color color) {
        casillas[fila][columna].setBackground(color);
    }

    public void mostrarMenu() {
        getContentPane().removeAll();
        getContentPane().add(panelMenu);
        revalidate();
        repaint();
    }


    /**
     * Muestra la lista de movimientos realizados en notación algebraica.
     * @param movimientos lista de movimientos en notación algebraica
     */
    public void mostrarMovimientos(List<String> movimientos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < movimientos.size(); i++) {
            if (i % 2 == 0) {
                sb.append((i / 2 + 1)).append(". ");
            }
            sb.append(movimientos.get(i)).append(" ");
            if (i % 2 == 1) {
                sb.append("\n");
            }
        }
        areaMovimientos.setText(sb.toString());
    }


    /**
     * Gestiona las funciones del reloj de ajedrez.
     * Incluye métodos para iniciar, detener y cambiar el turno del reloj.
     */

    public void iniciarReloj() {
        if (modoJuego) {
            relojAjedrez.detenerReloj(); // Asegurarse de detener cualquier reloj anterior
            relojAjedrez.iniciarReloj(); // Iniciar nuevo reloj
        }
    }


    public void detenerReloj() {
        if (modoJuego) {
            relojAjedrez.detenerReloj();
        }
    }

    public void cambiarTurnoReloj() {
        if (modoJuego) {
            relojAjedrez.cambiarTurno();
            GestorSonido.reproducirSonidoMovimiento();
        }
    }

    /**
     * Establece el modo de juego actual.
     * @param modoJuego true para modo juego, false para modo visor
     */
    public void setModoJuego(boolean modoJuego) {
        this.modoJuego = modoJuego;
    }

    /**
     * Muestra un mensaje en una ventana de diálogo.
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}