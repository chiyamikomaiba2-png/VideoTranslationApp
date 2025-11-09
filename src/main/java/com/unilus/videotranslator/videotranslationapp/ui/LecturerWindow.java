package com.unilus.videotranslator.videotranslationapp.ui;

import com.unilus.videotranslator.videotranslationapp.audio.AudioRecorder;
import com.unilus.videotranslator.videotranslationapp.audio.OfflineSpeechRecognizer;
import com.unilus.videotranslator.videotranslationapp.video.VideoCaptureManager;
import com.unilus.videotranslator.videotranslationapp.video.ImageUtils;
import com.unilus.videotranslator.videotranslationapp.translation.TranslationEngine;
import com.unilus.videotranslator.videotranslationapp.database.DatabaseManager;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import com.unilus.videotranslator.videotranslationapp.audio.AudioRecorder;
import com.unilus.videotranslator.videotranslationapp.media.FFmpegMerger;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LecturerWindow extends JFrame {
    
    private AudioRecorder audioRecorder;
    private String currentAudioPath;

    private String lecturerName;
    private int lecturerId;
    private VideoCaptureManager videoCapture;
    private JLabel videoLabel;
    private JButton startVideoButton, stopVideoButton;
    private JButton startRecordButton, stopRecordButton;
    private JButton startAudioButton, stopAudioButton;
    private JButton viewRecordingsButton, logoutButton;
    private Timer videoTimer;
    private JTextArea recognizedTextArea;
    private JTextArea translatedTextArea;
    private OfflineSpeechRecognizer offlineRecognizer;
    
    private boolean isRecording = false;
    private VideoWriter videoWriter;
    private String currentRecordingPath;
    private PrintWriter transcriptWriter;
    
    public LecturerWindow(String lecturerName, int lecturerId, String fullName) {
        this.lecturerName = fullName != null ? fullName : lecturerName;
        this.lecturerId = lecturerId;
        
        setTitle("Lecturer Portal - " + this.lecturerName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        Color bgColor = new Color(240, 240, 245);
        Color panelColor = Color.WHITE;
        Color accentColor = new Color(70, 130, 180);
        
        getContentPane().setBackground(bgColor);
        videoCapture = new VideoCaptureManager();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelColor);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("👨‍🏫 Welcome, " + this.lecturerName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(accentColor);
        
        logoutButton = createStyledButton("Logout", new Color(198, 40, 40));
        logoutButton.addActionListener(e -> logout());
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBackground(panelColor);
        videoPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new TitledBorder(
                new LineBorder(accentColor, 2),
                "Live Lecture Feed",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                accentColor
            )
        ));
        
        videoLabel = new JLabel();
        videoLabel.setPreferredSize(new Dimension(640, 480));
        videoLabel.setHorizontalAlignment(JLabel.CENTER);
        videoLabel.setBackground(Color.BLACK);
        videoLabel.setOpaque(true);
        videoLabel.setText("No Video Feed");
        videoLabel.setForeground(Color.LIGHT_GRAY);
        
        videoPanel.add(videoLabel, BorderLayout.CENTER);
        add(videoPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(panelColor);
        controlPanel.setBorder(new TitledBorder("Lecture Controls"));
        
        startVideoButton = createStyledButton("▶ Start Video", new Color(46, 125, 50));
        stopVideoButton = createStyledButton("⏹ Stop Video", new Color(198, 40, 40));
        startRecordButton = createStyledButton("⏺ Start Recording", new Color(220, 20, 60));
        stopRecordButton = createStyledButton("⏹ Stop Recording", new Color(100, 100, 100));
        startAudioButton = createStyledButton("🎤 Start Audio", new Color(255, 152, 0));
        stopAudioButton = createStyledButton("⏹ Stop Audio", new Color(198, 40, 40));
        viewRecordingsButton = createStyledButton("📁 View Recordings", new Color(103, 58, 183));
        
        stopVideoButton.setEnabled(false);
        stopRecordButton.setEnabled(false);
        stopAudioButton.setEnabled(false);
        
        controlPanel.add(startVideoButton);
        controlPanel.add(stopVideoButton);
        controlPanel.add(startRecordButton);
        controlPanel.add(stopRecordButton);
        controlPanel.add(startAudioButton);
        controlPanel.add(stopAudioButton);
        controlPanel.add(viewRecordingsButton);
        
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.setBackground(bgColor);
        
        JPanel recognizedPanel = createTextPanel("Recognized Speech", accentColor);
        recognizedTextArea = new JTextArea(4, 30);
        recognizedTextArea.setLineWrap(true);
        recognizedTextArea.setWrapStyleWord(true);
        recognizedPanel.add(new JScrollPane(recognizedTextArea), BorderLayout.CENTER);
        
        JPanel translatedPanel = createTextPanel("Bemba Translation", new Color(156, 39, 176));
        translatedTextArea = new JTextArea(4, 30);
        translatedTextArea.setLineWrap(true);
        translatedTextArea.setWrapStyleWord(true);
        translatedTextArea.setEditable(false);
        translatedPanel.add(new JScrollPane(translatedTextArea), BorderLayout.CENTER);
        
        textPanel.add(recognizedPanel);
        textPanel.add(translatedPanel);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.NORTH);
        bottomPanel.add(textPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        startVideoButton.addActionListener(e -> startVideo());
        stopVideoButton.addActionListener(e -> stopVideo());
        startRecordButton.addActionListener(e -> startRecording());
        stopRecordButton.addActionListener(e -> stopRecording());
        startAudioButton.addActionListener(e -> startAudio());
        stopAudioButton.addActionListener(e -> stopAudio());
        viewRecordingsButton.addActionListener(e -> viewRecordings());
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void startVideo() {
        if (videoCapture.startCapture(0)) {
            videoTimer = new Timer(50, e -> {
                Mat mat = videoCapture.captureFrame();
                if (mat != null) {
                    BufferedImage img = matToBufferedImage(mat);
                    videoLabel.setText("");
                    videoLabel.setIcon(new ImageIcon(img));
                    
                    if (isRecording && videoWriter != null) {
                        videoWriter.write(mat);
                    }
                }
            });
            videoTimer.start();
            startVideoButton.setEnabled(false);
            stopVideoButton.setEnabled(true);
            startRecordButton.setEnabled(true);
        }
    }
    
    private void stopVideo() {
        if (videoTimer != null) videoTimer.stop();
        if (isRecording) stopRecording();
        videoCapture.stopCapture();
        videoLabel.setIcon(null);
        videoLabel.setText("No Video Feed");
        startVideoButton.setEnabled(true);
        stopVideoButton.setEnabled(false);
        startRecordButton.setEnabled(false);
    }
    
    private void startRecording() {
    try {
        File recordingsDir = new File("recordings");
        if (!recordingsDir.exists()) {
            recordingsDir.mkdir();
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        currentRecordingPath = "recordings/lecture_" + timestamp + ".avi";
        currentAudioPath = "recordings/lecture_" + timestamp + ".wav";
        String transcriptPath = "recordings/lecture_" + timestamp + "_transcript.txt";
        
        // Start video recording
        videoWriter = new VideoWriter(
            currentRecordingPath,
            VideoWriter.fourcc('M', 'J', 'P', 'G'),
            20.0,
            new org.opencv.core.Size(640, 480)
        );
        
        // Start audio recording
        audioRecorder = new AudioRecorder(new File(currentAudioPath));
        audioRecorder.startRecording();
        
        transcriptWriter = new PrintWriter(new FileWriter(transcriptPath));
        transcriptWriter.println("Lecture Recording - " + lecturerName);
        transcriptWriter.println("Date: " + new Date());
        transcriptWriter.println("=".repeat(50));
        transcriptWriter.println();
        
        isRecording = true;
        startRecordButton.setEnabled(false);
        stopRecordButton.setEnabled(true);
        
        JOptionPane.showMessageDialog(this,
            "Recording started!\nVideo: " + currentRecordingPath + 
            "\nAudio: " + currentAudioPath,
            "Recording",
            JOptionPane.INFORMATION_MESSAGE);
            
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Failed to start recording: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    
   private void stopRecording() {
    if (isRecording) {
        isRecording = false;
        
        // Stop video recording
        if (videoWriter != null) {
            videoWriter.release();
            videoWriter = null;
        }
        
        // Stop audio recording
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
            audioRecorder = null;
        }
        
        if (transcriptWriter != null) {
            transcriptWriter.close();
            transcriptWriter = null;
        }
        
        startRecordButton.setEnabled(true);
        stopRecordButton.setEnabled(false);
        
        // Show progress dialog
        JOptionPane.showMessageDialog(this,
            "Processing recording...\nMerging audio and video...",
            "Processing",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Merge audio and video in background thread
        Thread mergeThread = new Thread(() -> {
            try {
                String outputFile = currentRecordingPath.replace(".avi", ".mp4");
                FFmpegMerger merger = new FFmpegMerger();
                
                boolean success = merger.mergeAudioVideo(
                    currentRecordingPath,
                    currentAudioPath,
                    outputFile
                );
                
                if (success) {
                    // Save to database
                    File recordingFile = new File(outputFile);
                    String title = "Lecture - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                    String transcriptPath = currentRecordingPath.replace(".avi", "_transcript.txt");
                    
                    DatabaseManager.saveRecording(
                        lecturerId,
                        title,
                        recordingFile.getName(),
                        new File(transcriptPath).getName(),
                        0,
                        recordingFile.length()
                    );
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "✅ Recording saved successfully!\n\n" +
                            "Video: " + outputFile + "\n" +
                            "Size: " + (recordingFile.length() / 1024 / 1024) + " MB",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "❌ Failed to merge audio and video\n\n" +
                            "Make sure FFmpeg is installed and in your system PATH\n" +
                            "Download from: https://ffmpeg.org/download.html",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        
        mergeThread.setDaemon(true);
        mergeThread.start();
    }
}

    
    private void startAudio() {
        startAudioButton.setEnabled(false);
        stopAudioButton.setEnabled(true);
        recognizedTextArea.setText("");
        translatedTextArea.setText("");
        
        offlineRecognizer = new OfflineSpeechRecognizer();
        offlineRecognizer.startListening(new OfflineSpeechRecognizer.SpeechCallback() {
            @Override
            public void onSpeechRecognized(String text) {
                SwingUtilities.invokeLater(() -> {
                    recognizedTextArea.append(text + "\n");
                    
                    TranslationEngine engine = new TranslationEngine();
                    String translated = engine.translate(text, "en", "bem");
                    translatedTextArea.append(translated + "\n");
                    
                    if (isRecording && transcriptWriter != null) {
                        transcriptWriter.println("English: " + text);
                        transcriptWriter.println("Bemba: " + translated);
                        transcriptWriter.println();
                        transcriptWriter.flush();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    recognizedTextArea.setText("Error: " + error);
                });
            }
        });
    }
    
    private void stopAudio() {
        if (offlineRecognizer != null) {
            offlineRecognizer.stopListening();
        }
        startAudioButton.setEnabled(true);
        stopAudioButton.setEnabled(false);
    }
    
    private void viewRecordings() {
        File recordingsDir = new File("recordings");
        if (!recordingsDir.exists() || recordingsDir.listFiles() == null) {
            JOptionPane.showMessageDialog(this,
                "No recordings found",
                "Recordings",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        File[] files = recordingsDir.listFiles((dir, name) -> name.endsWith(".avi"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this,
                "No recordings found",
                "Recordings",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DefaultListModel<String> model = new DefaultListModel<>();
        for (File f : files) {
            model.addElement(f.getName());
        }
        
        JList<String> fileList = new JList<>(model);
        JOptionPane.showMessageDialog(this,
            new JScrollPane(fileList),
            "Recorded Lectures",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginWindow();
        }
    }
    
    private BufferedImage matToBufferedImage(Mat mat) {
        Mat rgbMat = new Mat();
        if (mat.channels() == 3) {
            Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);
        } else {
            rgbMat = mat;
        }
        int width = rgbMat.width(), height = rgbMat.height(), channels = rgbMat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        rgbMat.get(0, 0, sourcePixels);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, sourcePixels);
        return image;
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
}
