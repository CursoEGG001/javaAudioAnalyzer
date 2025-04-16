/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer.controller;

/**
 *
 * @author pc
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import local.javademo.soundanalizer.audioanalyzer.view.AudioVisualizerPanel;
import local.javademo.soundanalizer.audioanalyzer.port.AudioCapturePort;
import local.javademo.soundanalizer.audioanalyzer.model.AudioData;
import local.javademo.soundanalizer.audioanalyzer.AudioDataListener;

/**
 * For the presentation of the analized data to be in a graph that show in real time the buffer of audio.
 * @author pc
 */
public class AudioAnalyzerController implements AudioDataListener {

    private final AudioData model;
    private final AudioVisualizerPanel visualizer;
    private final AudioCapturePort audioCapture;
    private final JComboBox<Mixer.Info> inputSelector;
    private final JFrame frame;

    /**
     * Sets the User Interface and Listeners in the mixer in charge of passing data for visualization.
     * @param audioCapture is the Port to use
     */
    public AudioAnalyzerController(AudioCapturePort audioCapture) {
        this.audioCapture = audioCapture;
        this.model = new AudioData(512);
        this.visualizer = new AudioVisualizerPanel();
        this.frame = new JFrame("Analizador de Audio");
        this.inputSelector = new JComboBox<>(audioCapture.getAvailableInputs());

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(512, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        controlPanel.add(new JLabel("Entrada de audio:"));
        controlPanel.add(inputSelector);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(visualizer, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.pack();
    }

    private void setupListeners() {
        audioCapture.addAudioDataListener(this);

        inputSelector.addActionListener((ActionEvent e) -> {
            Mixer.Info selectedMixer = (Mixer.Info) inputSelector.getSelectedItem();
            audioCapture.selectInput(selectedMixer);
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                audioCapture.stopCapture();
            }
        });
    }

    /**
     *
     * @param audioData is the data to be used in the graphs
     */
    @Override
    public void onAudioDataReceived(float[] audioData) {
        model.updateSamples(audioData);
        SwingUtilities.invokeLater(() -> visualizer.updateAudioData(audioData));
    }

    /**
     * set the frame on visibility and init capture of data.
     */
    public void start() {
        frame.setVisible(true);
        audioCapture.startCapture();
    }
}
