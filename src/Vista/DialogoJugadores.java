package Vista;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para ingresar los nombres de los jugadores de ajedrez.
 * Permite introducir los nombres para las piezas blancas y negras.
 */
public class DialogoJugadores extends JDialog {
    private JTextField nombreBlancas;
    private JTextField nombreNegras;
    private boolean aceptado;

    /**
     * Constructor del diálogo.
     *
     * @param parent Ventana padre sobre la que se mostrará el diálogo
     */
    public DialogoJugadores(Frame parent) {
        super(parent, "Nombres de Jugadores", true);
        inicializarComponentes();
    }

    /**
     * Inicializa y configura los componentes del diálogo.
     * Crea los campos de texto, etiquetas y botones, y los organiza
     * usando GridBagLayout.
     */
    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        nombreBlancas = new JTextField(20);
        nombreNegras = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Jugador Blancas:"), gbc);
        gbc.gridx = 1;
        add(nombreBlancas, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Jugador Negras:"), gbc);
        gbc.gridx = 1;
        add(nombreNegras, gbc);

        JPanel panelBotones = new JPanel();
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            if (validarNombres()) {
                aceptado = true;
                dispose();
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(panelBotones, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Valida que ambos campos de nombre no estén vacíos.
     * Muestra un mensaje de error si algún campo está vacío.
     *
     * @return true si ambos nombres son válidos, false en caso contrario
     */
    private boolean validarNombres() {
        if (nombreBlancas.getText().trim().isEmpty() ||
                nombreNegras.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese nombres para ambos jugadores",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * @return El nombre ingresado para el jugador de las piezas blancas
     */
    public String getNombreBlancas() {
        return nombreBlancas.getText().trim();
    }

    /**
     * @return El nombre ingresado para el jugador de las piezas negras
     */
    public String getNombreNegras() {
        return nombreNegras.getText().trim();
    }

    /**
     * @return true si el usuario presionó Aceptar, false si presionó Cancelar
     */
    public boolean isAceptado() {
        return aceptado;
    }
}