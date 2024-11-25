package Modelo;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Gestiona la reproducción de efectos de sonido para el juego de ajedrez.
 * Maneja sonidos para movimientos y victoria.
 */
public class GestorSonido {
    /** Ruta base donde se encuentran los archivos de sonido */
    private static final String RUTA_SONIDOS = "src/sonidos/";
    /** Nombre del archivo de sonido para movimientos */
    private static final String SONIDO_MOVIMIENTO = "movimiento.wav";
    /** Nombre del archivo de sonido para victoria */
    private static final String SONIDO_VICTORIA = "victoria.wav";

    /**
     * Reproduce el sonido asociado al movimiento de una pieza.
     */
    public static void reproducirSonidoMovimiento() {
        reproducirSonido(SONIDO_MOVIMIENTO);
    }

    /**
     * Reproduce el sonido asociado a una victoria.
     */
    public static void reproducirSonidoVictoria() {
        reproducirSonido(SONIDO_VICTORIA);
    }

    /**
     * Método auxiliar que maneja la reproducción de archivos de sonido.
     * @param nombreArchivo Nombre del archivo WAV a reproducir
     */
    private static void reproducirSonido(String nombreArchivo) {
        try {
            File archivoSonido = new File(RUTA_SONIDOS + nombreArchivo);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoSonido);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}