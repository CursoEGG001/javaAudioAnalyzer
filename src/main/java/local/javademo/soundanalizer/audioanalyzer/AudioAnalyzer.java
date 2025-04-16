///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// */
//package local.javademo.soundanalizer.audioanalyzer;
//
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.image.BufferedImage;
//import java.util.Arrays;
//import java.util.concurrent.ExecutionException;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.DataLine;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.TargetDataLine;
//import javax.swing.BorderFactory;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.SwingUtilities;
//import javax.swing.SwingWorker;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//
///**
// *
// * @author pc
// */
//public class AudioAnalyzer extends JFrame {
//
//    private static final int BUFFER_SIZE = 480;
//    private final AudioVisualizer visualizer;
//    private volatile boolean running = true;
//
//    public AudioAnalyzer() {
//        setTitle("Real-time Audio Analyzer");
//        setSize(512, 240);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        visualizer = new AudioVisualizer();
//        visualizer.setBorder(
//                BorderFactory.createCompoundBorder(
//                        BorderFactory.createRaisedBevelBorder(),
//                        BorderFactory.createTitledBorder("Data View")
//                )
//        );
//        add(visualizer);
//
//        // Start audio capture using SwingWorker
//        new AudioCaptureWorker().execute();
//    }
//
//    private class AudioCaptureWorker extends SwingWorker<Void, Void> {
//
//        @Override
//        protected Void doInBackground() throws Exception {
//            captureAudio();
//            return null;
//        }
//
//        @Override
//        protected void done() {
//            // Handle any exceptions that occurred during the background task
//            try {
//                get();
//
//            } catch (InterruptedException | ExecutionException e) {
//
//                System.out.println("Exception in audio capture: " + e.getMessage());
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    private void captureAudio() {
//        try {
//            // Audio format settings
//            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
//            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//
//            if (!AudioSystem.isLineSupported(info)) {
//                throw new LineUnavailableException("Line not supported");
//            }
//
//            try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
//
//                line.open(format);
//
//                for (var mixer : AudioSystem.getMixerInfo()) {
//                    for (var targetLine : AudioSystem.getMixer(mixer).getTargetLines()) {
//                        //Solo funciona porque no estás usando otra linea.
//                        if (targetLine.isOpen()) {
//                            System.out.println("Mixer: " + mixer.toString());
//                            System.out.println("Línea: "
//                                    + (targetLine.getLineInfo().equals(line.getLineInfo())
//                                    ? line.getLineInfo().toString()
//                                    : "No encontrada")
//                            );
//                        } else {
//                            System.out.println("Línea cerrada");
//                        }
//                    }
//                }
//
//                line.start();
//
//                byte[] buffer = new byte[BUFFER_SIZE];
//                float[] audioData = new float[BUFFER_SIZE / 2];
//
//                while (running) {
//                    int count = line.read(buffer, 0, buffer.length);
//
//                    if (count > 0) {
//                        // Convert bytes to float array
//                        for (int i = 0; i < count / 2 && i < audioData.length; i++) {
//                            int value = (buffer[2 * i + 1] << 8) | (buffer[2 * i] & 0xFF);
//                            audioData[i] = value * 2 / 65535.0f;
//                        }
//
//                        // Update visualizer
//                        visualizer.updateAudioData(audioData);
//                    }
//                }
//
//                line.stop();
//            }
//
//        } catch (LineUnavailableException e) {
//            System.out.println("Excepciones encontradas: " + e.getMessage());
//        }
//    }
//
//    static class AudioVisualizer extends JPanel {
//
//        private float[] audioData = new float[BUFFER_SIZE / 2];
//        private static final int BAR_WIDTH = 2;
//
//        public AudioVisualizer() {
//            setBackground(Color.DARK_GRAY);
//        }
//
//        public void updateAudioData(float[] newData) {
//            audioData = Arrays.copyOf(newData, newData.length);
//            repaint();
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            Graphics2D g2d = (Graphics2D) g;
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);
//
//            int width = getWidth();
//            int height = getHeight();
//            int centerY = height / 2;
//
//            // Draw frequency bars
//            // Create an off-screen image for drawing bars
//            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//            Graphics2D imageGraphics = image.createGraphics();
//
//            int barCount = Math.min(width / BAR_WIDTH, audioData.length);
//            for (int i = 0; i < barCount; i++) {
//                int x = i * BAR_WIDTH;
//                float magnitude = Math.abs(audioData[i]);
//                int barHeight = (int) (magnitude * (height) / 2);
//
//                imageGraphics.setColor(Color.GREEN);
//                imageGraphics.fillRect(x + 1, centerY - (barHeight), BAR_WIDTH - 1, (barHeight) * 2);
//            }
//
//            // Draw the off-screen image onto the component
//            g2d.drawImage(image, 0, 0, null);
//
//            // Draw center line
//            g2d.setColor(Color.GRAY);
//            g2d.fillRect(0, centerY, width, 1);
//
//            // Dispose of resources (optional)
//            imageGraphics.dispose();
//        }
//    }
//
//    public static void main(String[] args) {
//
//        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
//            if ("Nimbus".equals(laf.getName())) {
//                try {
//                    UIManager.setLookAndFeel(laf.getClassName());
//                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
//                    System.out.println("Look and Feel not ready: " + e.getMessage());
//                }
//            }
//        }
//
//        SwingUtilities.invokeLater(() -> {
//            AudioAnalyzer analyzer = new AudioAnalyzer();
//            analyzer.setVisible(true);
//        });
//    }
//}
