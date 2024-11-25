/**
 * Clase principal que inicia la aplicación de ajedrez.
 * Configura el modelo, vista y controlador siguiendo el patrón MVC.
 */
import Modelo.*;
import Controlador.*;
import Vista.Vista;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            Controlador controlador = new Controlador(modelo, vista);
            vista.setVisible(true);
        });
    }
}