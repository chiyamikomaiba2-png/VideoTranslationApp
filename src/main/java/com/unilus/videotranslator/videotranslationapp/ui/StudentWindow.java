package com.unilus.videotranslator.videotranslationapp.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class StudentWindow extends JFrame {
    
    private String studentName;
    private int studentId;
    private JButton logoutButton;
    private JList<String> recordingList;
    private DefaultListModel<String> listModel;
    private JTextArea transcriptArea;
    private JButton playButton, viewTranscriptButton, downloadButton;
    
    public StudentWindow(String studentName, int studentId, String fullName) {
        this.studentName = fullName != null ? fullName : studentName;
        this.studentId = studentId;
        
        setTitle("Student Portal - " + this.studentName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        Color bgColor = new Color(240, 240, 245);
        Color panelColor = Color.WHITE;
        Color accentColor = new Color(70, 130, 180);
        
        getContentPane().setBackground(bgColor);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelColor);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("👨‍🎓 Welcome, " + this.studentName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(accentColor);
        
        logoutButton = createStyledButton("Logout", new Color(198, 40, 40));
        logoutButton.addActionListener(e -> logout());
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(panelColor);
        leftPanel.setBorder(new TitledBorder(
            new LineBorder(accentColor, 2),
            "Available Lectures",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            accentColor
        ));
        
        listModel = new DefaultListModel<>();
        recordingList = new JList<>(listModel);
        recordingList.setFont(new Font("Arial", Font.PLAIN, 12));
        recordingList.addListSelectionListener(e -> loadTranscript());
        
        JScrollPane scrollPane = new JScrollPane(recordingList);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel leftControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        leftControlPanel.setBackground(panelColor);
        
        JButton refreshButton = createStyledButton("🔄 Refresh", accentColor);
        refreshButton.addActionListener(e -> refreshRecordings());
        
        leftControlPanel.add(refreshButton);
        leftPanel.add(leftControlPanel, BorderLayout.SOUTH);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(panelColor);
        rightPanel.setBorder(new TitledBorder(
            new LineBorder(new Color(156, 39, 176), 2),
            "Lecture Details",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(156, 39, 176)
        ));
        
        transcriptArea = new JTextArea();
        transcriptArea.setFont(new Font("Arial", Font.PLAIN, 12));
        transcriptArea.setLineWrap(true);
        transcriptArea.setWrapStyleWord(true);
        transcriptArea.setEditable(false);
        transcriptArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane transcriptScroll = new JScrollPane(transcriptArea);
        transcriptScroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        rightPanel.add(transcriptScroll, BorderLayout.CENTER);
        
        JPanel rightControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        rightControlPanel.setBackground(panelColor);
        
        playButton = createStyledButton("▶ Play Video", new Color(46, 125, 50));
        playButton.setEnabled(false);
        playButton.addActionListener(e -> playVideo());
        
        viewTranscriptButton = createStyledButton("📄 Full Transcript", accentColor);
        viewTranscriptButton.setEnabled(false);
        viewTranscriptButton.addActionListener(e -> viewFullTranscript());
        
        downloadButton = createStyledButton("⬇ Download", new Color(255, 152, 0));
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(e -> downloadLecture());
        
        rightControlPanel.add(playButton);
        rightControlPanel.add(viewTranscriptButton);
        rightControlPanel.add(downloadButton);
        rightPanel.add(rightControlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);
        
        refreshRecordings();
        
        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void refreshRecordings() {
        listModel.clear();
        
        File recordingsDir = new File("recordings");
        if (!recordingsDir.exists()) {
            recordingsDir.mkdir();
        }
        
        File[] files = recordingsDir.listFiles((dir, name) -> name.endsWith(".avi"));
        if (files != null) {
            for (File f : files) {
                listModel.addElement(f.getName());
            }
        }
        
        if (listModel.size() == 0) {
            listModel.addElement("No lectures available yet");
        }
    }
    
    private void loadTranscript() {
        int selectedIndex = recordingList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < listModel.size()) {
            String selectedFile = listModel.get(selectedIndex);
            
            if (selectedFile.equals("No lectures available yet")) {
                transcriptArea.setText("No lecture selected.");
                playButton.setEnabled(false);
                viewTranscriptButton.setEnabled(false);
                downloadButton.setEnabled(false);
                return;
            }
            
            playButton.setEnabled(true);
            viewTranscriptButton.setEnabled(true);
            downloadButton.setEnabled(true);
            
            String transcriptFileName = selectedFile.replace(".avi", "_transcript.txt");
            File transcriptFile = new File("recordings/" + transcriptFileName);
            
            if (transcriptFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(transcriptFile.toPath()));
                    transcriptArea.setText(content);
                } catch (IOException ex) {
                    transcriptArea.setText("Could not load transcript.");
                }
            } else {
                transcriptArea.setText("Transcript not available for this lecture.");
            }
        }
    }
    
    private void playVideo() {
        int selectedIndex = recordingList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < listModel.size()) {
            String selectedFile = listModel.get(selectedIndex);
            String videoPath = new File("recordings").getAbsolutePath() + File.separator + selectedFile;
            
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", videoPath});
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    Runtime.getRuntime().exec(new String[]{"open", videoPath});
                } else {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", videoPath});
                }
                
                JOptionPane.showMessageDialog(this,
                    "Playing: " + selectedFile,
                    "Video Player",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Could not open video player",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewFullTranscript() {
        int selectedIndex = recordingList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedFile = listModel.get(selectedIndex);
            String transcriptFileName = selectedFile.replace(".avi", "_transcript.txt");
            File transcriptFile = new File("recordings/" + transcriptFileName);
            
            if (transcriptFile.exists()) {
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", transcriptFile.getAbsolutePath()});
                    } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                        Runtime.getRuntime().exec(new String[]{"open", transcriptFile.getAbsolutePath()});
                    } else {
                        Runtime.getRuntime().exec(new String[]{"xdg-open", transcriptFile.getAbsolutePath()});
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Could not open file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void downloadLecture() {
        int selectedIndex = recordingList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedFile = listModel.get(selectedIndex);
            String sourcePath = "recordings/" + selectedFile;
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(selectedFile));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File destFile = fileChooser.getSelectedFile();
                try {
                    Files.copy(Paths.get(sourcePath), Paths.get(destFile.getAbsolutePath()),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    JOptionPane.showMessageDialog(this,
                        "Lecture downloaded successfully!",
                        "Download Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Failed to download: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
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
}
