/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer;

/**
 *
 * @author pc
 */
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import local.javademo.soundanalizer.audioanalyzer.controller.AudioAnalyzerController;
import local.javademo.soundanalizer.audioanalyzer.adapter.JavaSoundCaptureAdapter;
import local.javademo.soundanalizer.audioanalyzer.port.AudioCapturePort;

public class AudioAnalyzerApp {

    /**
     * App ready for selection of an audio input and presents a data view with the data incoming from the input selected.
     *
     * @param args in the line of commands not change anything in the app
     */
    public static void main(String[] args) {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(laf.getName())) {
                try {
                    UIManager.setLookAndFeel(laf.getClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                    System.err.println("Look and Feel not ready: " + e.getMessage());
                }
            }
        }

        SwingUtilities.invokeLater(() -> {
            AudioCapturePort audioCapture = new JavaSoundCaptureAdapter();
            AudioAnalyzerController controller = new AudioAnalyzerController(audioCapture);
            controller.start();
        });
    }
}
