/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer.port;

/**
 *
 * @author pc
 */
import javax.sound.sampled.Mixer;
import local.javademo.soundanalizer.audioanalyzer.AudioDataListener;

/**
 * This enables the mixer in th input and list the audio inputs
 *
 * @author pc
 */
public interface AudioCapturePort {

    /**
     * Init of audio capture
     */
    void startCapture();

    /**
     * Finalization of the capture of audio
     */
    void stopCapture();

    /**
     *
     * @return mixer's capable of audio input
     */
    Mixer.Info[] getAvailableInputs();

    /**
     * selection of the input of audio
     *
     * @param mixer is a capable audio input
     */
    void selectInput(Mixer.Info mixer);

    /**
     * Audio processing signals
     *
     * @param listener for the data to be used.
     */
    void addAudioDataListener(AudioDataListener listener);
}
