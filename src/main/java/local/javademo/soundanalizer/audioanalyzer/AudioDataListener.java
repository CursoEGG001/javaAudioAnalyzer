/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer;

/**
 *
 * @author pc
 */
public interface AudioDataListener {
    void onAudioDataReceived(float[] audioData);
}