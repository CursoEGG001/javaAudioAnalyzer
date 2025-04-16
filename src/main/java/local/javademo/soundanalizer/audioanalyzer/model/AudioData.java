/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer.model;

/**
 *
 * @author pc
 */
public class AudioData {

    private float[] audioSamples;
    private final int bufferSize;

    /**
     * Audio information for the graphics creation.
     *
     * @param bufferSize of the data recorded
     */
    public AudioData(int bufferSize) {
        this.bufferSize = bufferSize;
        this.audioSamples = new float[bufferSize];
    }

    /**
     * Update of the audio data.
     *
     * @param newSamples is the new information from buffers
     */
    public void updateSamples(float[] newSamples) {
        this.audioSamples = newSamples.clone();
    }

    /**
     * Method useful for sample manipulation.
     *
     * @return array of data values to be used
     */
    public float[] getSamples() {
        return audioSamples.clone();
    }

    /**
     *
     * determines the size of the buffer of audio.
     *
     * @return the size requiered
     */
    public int getBufferSize() {
        return bufferSize;
    }
}
