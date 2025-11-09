/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.ui;

import com.unilus.videotranslator.videotranslationapp.audio.OfflineSpeechRecognizer;
import com.unilus.videotranslator.videotranslationapp.video.VideoCaptureManager;
import com.unilus.videotranslator.videotranslationapp.video.ImageUtils;
import com.unilus.videotranslator.videotranslationapp.network.UDPTransmitter;
import com.unilus.videotranslator.videotranslationapp.network.UDPReceiver;
import com.unilus.videotranslator.videotranslationapp.translation.TranslationEngine;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {

    // VIDEO CAPTURE VARIABLES
    private VideoCaptureManager videoCapture;
    private JLabel videoLabel;
    private JButton startButton, stopButton, sendButton, receiveButton;
    private Timer videoTimer;
    private volatile boolean sending = false, receiving = false;
    private Thread sendThread, receiveThread;

    // AUDIO CAPTURE VARIABLES
    private JButton startAudioButton;
    private JButton stopAudioButton;
    private OfflineSpeechRecognizer offlineRecognizer;
    private JTextArea recognizedTextArea;
    private JTextArea translatedTextArea;

    public MainWindow() {
        setTitle("Video Translation Application - UNILUS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Set color scheme
        Color bgColor = new Color(240, 240, 245);
        Color panelColor = Color.WHITE;
        Color accentColor = new Color(70, 130, 180); // Steel blue
        
        getContentPane().setBackground(bgColor);

        videoCapture = new VideoCaptureManager();

        // --- VIDEO PANEL (Center) ---
        JPanel videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBackground(panelColor);
        videoPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new TitledBorder(
                new LineBorder(accentColor, 2),
                "Video Feed",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                accentColor
            )
        ));

        videoLabel = new JLabel();
        videoLabel.setPreferredSize(new Dimension(640, 480));
        videoLabel.setHorizontalAlignment(JLabel.CENTER);
        videoLabel.setVerticalAlignment(JLabel.CENTER);
        videoLabel.setBackground(Color.BLACK);
        videoLabel.setOpaque(true);
        videoLabel.setText("No Video Feed");
        videoLabel.setForeground(Color.LIGHT_GRAY);
        videoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        videoPanel.add(videoLabel, BorderLayout.CENTER);
        add(videoPanel, BorderLayout.CENTER);

        // --- CONTROL PANEL (Top) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(panelColor);
        controlPanel.setBorder(new CompoundBorder(
            new EmptyBorder(5, 10, 5, 10),
            new TitledBorder(
                new LineBorder(accentColor, 2),
                "Controls",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                accentColor
            )
        ));

        // Style buttons
        startButton = createStyledButton("▶ Start Video", new Color(46, 125, 50));
        stopButton = createStyledButton("⏹ Stop Video", new Color(198, 40, 40));
        sendButton = createStyledButton("📤 Send Video", accentColor);
        receiveButton = createStyledButton("📥 Receive Video", accentColor);
        startAudioButton = createStyledButton("🎤 Start Audio", new Color(255, 152, 0));
        stopAudioButton = createStyledButton("⏹ Stop Audio", new Color(198, 40, 40));
        
        stopButton.setEnabled(false);
        sendButton.setEnabled(false);
        stopAudioButton.setEnabled(false);

        controlPanel.add(startAudioButton);
        controlPanel.add(stopAudioButton);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(sendButton);
        controlPanel.add(receiveButton);

        add(controlPanel, BorderLayout.NORTH);

        // --- TEXT PANEL (Bottom) ---
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.setBackground(bgColor);
        textPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        // Recognized Text Panel
        JPanel recognizedPanel = createTextPanel(
            "Recognized Speech (English)",
            accentColor
        );
        recognizedTextArea = new JTextArea(4, 30);
        recognizedTextArea.setFont(new Font("Arial", Font.PLAIN, 13));
        recognizedTextArea.setLineWrap(true);
        recognizedTextArea.setWrapStyleWord(true);
        recognizedTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane recognizedScroll = new JScrollPane(recognizedTextArea);
        recognizedScroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        recognizedPanel.add(recognizedScroll, BorderLayout.CENTER);

        // Translated Text Panel
        JPanel translatedPanel = createTextPanel(
            "Translated Text (Bemba)",
            new Color(156, 39, 176) // Purple
        );
        translatedTextArea = new JTextArea(4, 30);
        translatedTextArea.setFont(new Font("Arial", Font.PLAIN, 13));
        translatedTextArea.setLineWrap(true);
        translatedTextArea.setWrapStyleWord(true);
        translatedTextArea.setEditable(false);
        translatedTextArea.setBackground(new Color(250, 250, 250));
        translatedTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane translatedScroll = new JScrollPane(translatedTextArea);
        translatedScroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        translatedPanel.add(translatedScroll, BorderLayout.CENTER);

        textPanel.add(recognizedPanel);
        textPanel.add(translatedPanel);

        add(textPanel, BorderLayout.SOUTH);

        // --- Button Actions ---
        startButton.addActionListener(e -> startVideo());
        stopButton.addActionListener(e -> stopVideo());
        sendButton.addActionListener(e -> startSending());
        receiveButton.addActionListener(e -> startReceiving());
        startAudioButton.addActionListener(e -> startAudioCapture());
        stopAudioButton.addActionListener(e -> stopAudioCapture());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(bgColor.darker(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createTextPanel(String title, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder(
            new LineBorder(titleColor, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            titleColor
        ));
        return panel;
    }

    private void startVideo() {
        if (videoCapture.startCapture(0)) {
            videoTimer = new Timer(50, e -> {
                Mat mat = videoCapture.captureFrame();
                if (mat != null) {
                    BufferedImage img = matToBufferedImage(mat);
                    videoLabel.setText("");
                    videoLabel.setIcon(new ImageIcon(img));
                }
            });
            videoTimer.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            sendButton.setEnabled(true);
        }
    }

    private void stopVideo() {
        if (videoTimer != null) videoTimer.stop();
        videoCapture.stopCapture();
        videoLabel.setIcon(null);
        videoLabel.setText("No Video Feed");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        sendButton.setEnabled(false);
        stopSending();
    }

    private BufferedImage matToBufferedImage(Mat mat) {
    // Convert from BGR to RGB if mat has 3 channels
    Mat rgbMat = new Mat();
    if (mat.channels() == 3) {
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);
    } else {
        rgbMat = mat;
    }
    int width = rgbMat.width(), height = rgbMat.height(), channels = rgbMat.channels();
    byte[] sourcePixels = new byte[width * height * channels];
    rgbMat.get(0, 0, sourcePixels);
    BufferedImage image;
    if (channels == 3)
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    else
        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    image.getRaster().setDataElements(0, 0, width, height, sourcePixels);
    return image;
}

    private void startSending() {
        sending = true;
        sendThread = new Thread(() -> {
            try {
                UDPTransmitter tx = new UDPTransmitter("127.0.0.1", 6000);
                while (sending) {
                    Mat mat = videoCapture.captureFrame();
                    if (mat != null) {
                        BufferedImage img = matToBufferedImage(mat);
                        byte[] bytes = ImageUtils.bufferedImageToBytes(img);
                        if (bytes.length <= 65507)
                            tx.send(bytes);
                    }
                    Thread.sleep(50);
                }
                tx.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error sending video: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        sendThread.start();
        sendButton.setEnabled(false);
    }

    private void stopSending() {
        sending = false;
        if (sendThread != null) sendThread.interrupt();
        sendButton.setEnabled(true);
    }

    private void startReceiving() {
        receiving = true;
        receiveThread = new Thread(() -> {
            try {
                UDPReceiver rx = new UDPReceiver(6000);
                while (receiving) {
                    byte[] bytes = rx.receive(65536);
                    BufferedImage img = ImageUtils.bytesToBufferedImage(bytes);
                    if (img != null) {
                        SwingUtilities.invokeLater(() -> {
                            videoLabel.setText("");
                            videoLabel.setIcon(new ImageIcon(img));
                        });
                    }
                }
                rx.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error receiving video: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        receiveThread.start();
        receiveButton.setEnabled(false);
    }

    private void startAudioCapture() {
        startAudioButton.setEnabled(false);
        stopAudioButton.setEnabled(true);

        recognizedTextArea.setText("");
        translatedTextArea.setText("");

        offlineRecognizer = new OfflineSpeechRecognizer();
        offlineRecognizer.startListening(new OfflineSpeechRecognizer.SpeechCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                SwingUtilities.invokeLater(() -> {
                    recognizedTextArea.setText(text);

                    TranslationEngine engine = new TranslationEngine();
                    String translated = engine.translate(text, "en", "bem");
                    translatedTextArea.setText(translated);
                });
            }

            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    recognizedTextArea.setText("Error: " + error);
                    translatedTextArea.setText("");
                });
            }
        });

        System.out.println("🎤 Offline speech recognition started");
    }

    private void stopAudioCapture() {
        if (offlineRecognizer != null) {
            offlineRecognizer.stopListening();
        }
        startAudioButton.setEnabled(true);
        stopAudioButton.setEnabled(false);
        System.out.println("🛑 Speech recognition stopped");
    }
}
