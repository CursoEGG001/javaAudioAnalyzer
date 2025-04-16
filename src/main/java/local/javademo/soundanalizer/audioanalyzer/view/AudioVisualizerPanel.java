/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.javademo.soundanalizer.audioanalyzer.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;

/**
 *
 * @author pc
 */
public class AudioVisualizerPanel extends JPanel {

    private float[] audioData;
    private static final int BAR_WIDTH = 3;
    private BufferedImage visualizationImage; // Precomputed visualization image
    private final ExecutorService executor;

    public AudioVisualizerPanel() {
        this.audioData = new float[0];
        this.executor = Executors.newSingleThreadExecutor(); // Executor for background tasks
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createTitledBorder("Data View")
        ));
    }

    public void updateAudioData(float[] newData) {
        audioData = newData.clone();
        if (audioData.length != 0) {
            // Generate visualization in a background thread
            generateVisualizationImage();
        } else {
            visualizationImage = null; // Clear the visualization if no data
            repaint();
        }
    }

    private void generateVisualizationImage() {
        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;

        executor.submit(() -> {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int barCount = Math.min(width / BAR_WIDTH, audioData.length);
            for (int i = 0; i < barCount; i++) {
                int x = i * BAR_WIDTH;
                float magnitude = Math.abs(audioData[i]);
                int barHeight = (int) (magnitude * (height) / 2);

                g2d.setColor(Color.GREEN);
                g2d.fillRect(x + 1, centerY - barHeight, BAR_WIDTH - 1, barHeight * 2);
            }
            g2d.dispose();

            // Update the visualization image and repaint the panel
            SwingUtilities.invokeLater(() -> {
                visualizationImage = image;
                repaint();
            });
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerY = getHeight() / 2;

        // Draw the precomputed visualization image
        if (visualizationImage != null) {
            g2d.drawImage(visualizationImage, 0, 0, null);
        } else {
            // Placeholder when no visualization image is available
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("Sin Datos", getWidth() / 2 - 20, getHeight() / 2);
        }

        // Draw the center line
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, centerY, getWidth(), 1);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        executor.shutdownNow(); // Ensure the executor shuts down when the panel is removed
    }
}
