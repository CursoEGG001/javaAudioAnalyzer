/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer.adapter;

/**
 *
 * @author pc
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import local.javademo.soundanalizer.audioanalyzer.port.AudioCapturePort;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import local.javademo.soundanalizer.audioanalyzer.AudioDataListener;

/**
 * Connects the information and functionalities with the audio analisys and services used in the processing.
 *
 * @author pc
 */
public class JavaSoundCaptureAdapter implements AudioCapturePort {

    private static final int BUFFER_SIZE = 512;
    private static final AudioFormat FORMAT = new AudioFormat(44100, 16, 1, true, false);

    private volatile boolean running = false;
    private TargetDataLine line;
    private final List<AudioDataListener> listeners;
    private final ExecutorService executorService;
    private Mixer.Info selectedMixer;

    /**
     * Creates the listeners list and init the threads for the capture.
     */
    public JavaSoundCaptureAdapter() {
        this.listeners = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(BUFFER_SIZE);
    }

    /**
     * send the signal for capture start.
     */
    @Override
    public void startCapture() {
        if (running) {
            return;
        }

        running = true;
        executorService.submit(this::captureAudio);
    }

    /**
     * stop the thread capture of data and the line.
     */
    @Override
    public void stopCapture() {
        running = false;
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    /**
     * Send information of mixer capables of doing the audio data input.
     *
     * @return a list of mixer capables of audio input.
     */
    @Override
    public Mixer.Info[] getAvailableInputs() {
        return Arrays.stream(
                AudioSystem.getMixerInfo())
                .filter((Mixer.Info mixerInfo) -> {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    return Arrays.stream(mixer.getTargetLineInfo())
                            .anyMatch((Line.Info lineInfo) -> lineInfo.getLineClass().equals(TargetDataLine.class));
                })
                .toArray(Mixer.Info[]::new);
    }

    /**
     * Choose the input audio for data presentation.
     *
     * @param mixer in charge of providing data
     */
    @Override
    public void selectInput(Mixer.Info mixer) {
        this.selectedMixer = mixer;
        if (running) {
            stopCapture();
            startCapture();
        }
    }

    /**
     * used for audio data processing.
     *
     * @param listener for the data manipulation
     */
    @Override
    public void addAudioDataListener(AudioDataListener listener) {
        listeners.add(listener);
    }

    private void captureAudio() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);

            if (AudioSystem.getMixer(selectedMixer).isLineSupported(info)) {
                Mixer mixer = AudioSystem.getMixer(selectedMixer);
                line = (TargetDataLine) mixer.getLine(info);
            } else {
                line = (TargetDataLine) AudioSystem.getLine(info);
            }

            line.open(FORMAT);
            line.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            float[] audioData = new float[BUFFER_SIZE / 2];

            while (running) {
                int count = line.read(buffer, 0, buffer.length);

                if (count > 0) {
                    // Convert bytes to float array
                    for (int i = 0; i < count / 2 && i < audioData.length; i++) {
                        int value = (buffer[2 * i + 1] << 8) | (buffer[2 * i] & 0xFF);
                        audioData[i] = value * 2 / 65535.0f;
                    }

                    // Notify listeners
                    listeners.forEach(listener
                            -> listener.onAudioDataReceived(audioData.clone())
                    );
                }
            }
        } catch (LineUnavailableException e) {
            System.err.println("Error al capturar el audio: " + e.getMessage());
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }
        }
    }
}
