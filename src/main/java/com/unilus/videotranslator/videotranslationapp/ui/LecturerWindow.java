package com.unilus.videotranslator.videotranslationapp.ui;

import com.unilus.videotranslator.videotranslationapp.audio.AudioRecorder;
import com.unilus.videotranslator.videotranslationapp.translation.TranslationEngine;
import com.unilus.videotranslator.videotranslationapp.database.DatabaseManager;
import com.unilus.videotranslator.videotranslationapp.media.FFmpegMerger;
import com.unilus.videotranslator.videotranslationapp.video.VideoCaptureManager;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LecturerWindow extends JFrame {
    private String lecturerName;
    private int lecturerId;
    private VideoCaptureManager videoCapture;
    private JLabel videoLabel;
    private JButton startVideoButton, stopVideoButton;
    private JButton startRecordButton, stopRecordButton;
    private JButton viewRecordingsButton, logoutButton;
    private Timer videoTimer;
    private JTextArea recognizedTextArea;
    private JTextArea translatedTextArea;
    private AudioRecorder audioRecorder;
    private VideoWriter videoWriter;
    private String currentRecordingPath;
    private String currentAudioPath;
    private boolean isRecording = false;

    public LecturerWindow(String lecturerName, int lecturerId, String fullName) {
        this.lecturerName = fullName != null ? fullName : lecturerName;
        this.lecturerId = lecturerId;
        setTitle("Lecturer Portal - " + this.lecturerName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        Color accentColor = new Color(70, 130, 180);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel welcomeLabel = new JLabel("👨‍🏫 Welcome, " + this.lecturerName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(accentColor);

        logoutButton = createStyledButton("Logout", new Color(198, 40, 40));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow();
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new TitledBorder(new LineBorder(accentColor, 2), "Live Lecture Feed",
                        TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), accentColor
                )
        ));
        videoLabel = new JLabel("No Video Feed", SwingConstants.CENTER);
        videoLabel.setBackground(Color.BLACK);
        videoLabel.setOpaque(true);
        videoPanel.add(videoLabel, BorderLayout.CENTER);
        add(videoPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new TitledBorder("Lecture Controls"));
        startVideoButton = createStyledButton("▶ Start Video", new Color(46, 125, 50));
        stopVideoButton = createStyledButton("⏹ Stop Video", new Color(198, 40, 40));
        startRecordButton = createStyledButton("⏺ Start Recording (Video + Audio)", new Color(220, 20, 60));
        stopRecordButton = createStyledButton("⏹ Stop Recording", new Color(100, 100, 100));
        viewRecordingsButton = createStyledButton("📁 View Recordings", new Color(103, 58, 183));
        stopVideoButton.setEnabled(false);
        stopRecordButton.setEnabled(false);

        controlPanel.add(startVideoButton);
        controlPanel.add(stopVideoButton);
        controlPanel.add(startRecordButton);
        controlPanel.add(stopRecordButton);
        controlPanel.add(viewRecordingsButton);

        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.setBackground(Color.WHITE);
        recognizedTextArea = new JTextArea(4, 30);
        recognizedTextArea.setLineWrap(true);
        recognizedTextArea.setWrapStyleWord(true);
        JScrollPane recScroll = new JScrollPane(recognizedTextArea);
        JPanel recognizedPanel = createTextPanel("Recognized Speech", accentColor);
        recognizedPanel.add(recScroll, BorderLayout.CENTER);

        translatedTextArea = new JTextArea(4, 30);
        translatedTextArea.setLineWrap(true);
        translatedTextArea.setWrapStyleWord(true);
        translatedTextArea.setEditable(false);
        JScrollPane transScroll = new JScrollPane(translatedTextArea);
        JPanel translatedPanel = createTextPanel("Bemba Translation", new Color(156, 39, 176));
        translatedPanel.add(transScroll, BorderLayout.CENTER);

        textPanel.add(recognizedPanel);
        textPanel.add(translatedPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.NORTH);
        bottomPanel.add(textPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        videoCapture = new VideoCaptureManager();
        startVideoButton.addActionListener(e -> startVideo());
        stopVideoButton.addActionListener(e -> stopVideo());
        startRecordButton.addActionListener(e -> startRecording());
        stopRecordButton.addActionListener(e -> stopRecording());
        viewRecordingsButton.addActionListener(e -> viewRecordings());

        pack();
        setSize(900, 650);
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
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File recordingsDir = new File("recordings");
            if (!recordingsDir.exists()) recordingsDir.mkdir();
            currentRecordingPath = "recordings/lecture_" + ts + ".avi";
            currentAudioPath = "recordings/lecture_" + ts + ".wav";
            // Start video (OpenCV)
            videoWriter = new VideoWriter(
                    currentRecordingPath,
                    VideoWriter.fourcc('M', 'J', 'P', 'G'),
                    20.0,
                    new org.opencv.core.Size(640, 480)
            );
            videoCapture.startCapture(0);
            videoTimer = new Timer(50, e -> {
                Mat mat = videoCapture.captureFrame();
                if (mat != null) {
                    BufferedImage img = matToBufferedImage(mat);
                    videoLabel.setText("");
                    videoLabel.setIcon(new ImageIcon(img));
                    if (isRecording && videoWriter != null)
                        videoWriter.write(mat);
                }
            });
            videoTimer.start();

            // Start audio
            audioRecorder = new AudioRecorder(new File(currentAudioPath));
            audioRecorder.startRecording();

            isRecording = true;
            startRecordButton.setEnabled(false);
            stopRecordButton.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Recording error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        if (videoTimer != null) videoTimer.stop();
        if (videoWriter != null) {
            videoWriter.release();
            videoWriter = null;
        }
        videoCapture.stopCapture();
        if (audioRecorder != null) audioRecorder.stopRecording();

        // Merge
        String outputFile = currentRecordingPath.replace(".avi", ".mp4");
        FFmpegMerger merger = new FFmpegMerger();
        boolean merged = merger.mergeAudioVideo(currentRecordingPath, currentAudioPath, outputFile);

        if (merged) {
            File rec = new File(outputFile);
            DatabaseManager.saveRecording(lecturerId, "Lecture - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), rec.getName(), "", 0, rec.length());
            JOptionPane.showMessageDialog(this, "Lecture saved/merged: " + outputFile, "Done", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to merge audio/video. Check ffmpeg path.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        startRecordButton.setEnabled(true);
        stopRecordButton.setEnabled(false);
    }

    private void viewRecordings() {
        File recordingsDir = new File("recordings");
        if (!recordingsDir.exists() || recordingsDir.listFiles() == null) {
            JOptionPane.showMessageDialog(this, "No recordings found", "Recordings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File[] files = recordingsDir.listFiles((dir, name) -> name.endsWith(".mp4"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "No merged recordings found", "Recordings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        DefaultListModel<String> model = new DefaultListModel<>();
        for (File f : files) model.addElement(f.getName());
        JList<String> fileList = new JList<>(model);
        JOptionPane.showMessageDialog(this, new JScrollPane(fileList), "Recorded Lectures", JOptionPane.PLAIN_MESSAGE);
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        Mat rgbMat = new Mat();
        if (mat.channels() == 3) Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);
        else rgbMat = mat;
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
