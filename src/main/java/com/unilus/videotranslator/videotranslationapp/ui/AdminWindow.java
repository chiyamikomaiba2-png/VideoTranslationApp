package com.unilus.videotranslator.videotranslationapp.ui;

import com.unilus.videotranslator.videotranslationapp.database.DatabaseManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminWindow extends JFrame {
    
    private String adminName;
    private int adminId;
    private JButton logoutButton;
    private JTable systemStatsTable;
    private JLabel totalLecturesLabel;
    private JLabel totalStudentsLabel;
    private JLabel totalLecturersLabel;
    private JButton clearCacheButton, backupButton;
    
    public AdminWindow(String adminName, int adminId, String fullName) {
        this.adminName = fullName != null ? fullName : adminName;
        this.adminId = adminId;
        
        setTitle("Admin Dashboard - " + this.adminName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        Color bgColor = new Color(240, 240, 245);
        Color panelColor = Color.WHITE;
        Color accentColor = new Color(70, 130, 180);
        
        getContentPane().setBackground(bgColor);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelColor);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("⚙️ Admin Dashboard - " + this.adminName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(accentColor);
        
        logoutButton = createStyledButton("Logout", new Color(198, 40, 40));
        logoutButton.addActionListener(e -> logout());
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(bgColor);
        
        totalLecturesLabel = new JLabel("0");
        totalStudentsLabel = new JLabel("0");
        totalLecturersLabel = new JLabel("0");
        
        statsPanel.add(createStatCard("📚 Total Lectures", totalLecturesLabel, accentColor));
        statsPanel.add(createStatCard("👨‍🎓 Total Students", totalStudentsLabel, new Color(76, 175, 80)));
        statsPanel.add(createStatCard("👨‍🏫 Total Lecturers", totalLecturersLabel, new Color(255, 152, 0)));
        
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(new TitledBorder(
            new LineBorder(accentColor, 2),
            "System Information",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            accentColor
        ));
        
        String[] columns = {"Property", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        systemStatsTable = new JTable(tableModel);
        systemStatsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        systemStatsTable.setRowHeight(25);
        
        tableModel.addRow(new Object[]{"Java Version", System.getProperty("java.version")});
        tableModel.addRow(new Object[]{"Operating System", System.getProperty("os.name")});
        tableModel.addRow(new Object[]{"System Architecture", System.getProperty("os.arch")});
        tableModel.addRow(new Object[]{"Available Processors", Runtime.getRuntime().availableProcessors()});
        tableModel.addRow(new Object[]{"Total Memory (MB)", Runtime.getRuntime().totalMemory() / 1024 / 1024});
        tableModel.addRow(new Object[]{"Free Memory (MB)", Runtime.getRuntime().freeMemory() / 1024 / 1024});
        tableModel.addRow(new Object[]{"Application Version", "1.0-SNAPSHOT"});
        tableModel.addRow(new Object[]{"Last Updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});
        
        JScrollPane tableScroll = new JScrollPane(systemStatsTable);
        tableScroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(panelColor);
        controlPanel.setBorder(new TitledBorder("Admin Controls"));
        
        clearCacheButton = createStyledButton("🗑️ Clear Cache", new Color(244, 67, 54));
        clearCacheButton.addActionListener(e -> clearCache());
        
        backupButton = createStyledButton("💾 Backup Data", new Color(103, 58, 183));
        backupButton.addActionListener(e -> backupData());
        
        JButton refreshButton = createStyledButton("🔄 Refresh", accentColor);
        refreshButton.addActionListener(e -> refreshStats());
        
        controlPanel.add(clearCacheButton);
        controlPanel.add(backupButton);
        controlPanel.add(refreshButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        refreshStats();
        
        pack();
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void refreshStats() {
        DatabaseManager.Statistics stats = DatabaseManager.getStatistics();
        
        totalLecturesLabel.setText(String.valueOf(stats.totalLectures));
        totalStudentsLabel.setText(String.valueOf(stats.totalStudents));
        totalLecturersLabel.setText(String.valueOf(stats.totalLecturers));
    }
    
    private void clearCache() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear the cache?\nThis cannot be undone.",
            "Clear Cache",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                File targetDir = new File("target");
                if (targetDir.exists()) {
                    deleteDirectory(targetDir);
                }
                JOptionPane.showMessageDialog(this,
                    "Cache cleared successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error clearing cache: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void backupData() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupPath = "backups/backup_" + timestamp;
            
            new File("backups").mkdirs();
            
            if (new File("recordings").exists()) {
                copyDirectory(new File("recordings"), new File(backupPath + "/recordings"));
            }
            
            JOptionPane.showMessageDialog(this,
                "Data backed up successfully to:\n" + backupPath,
                "Backup Complete",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error during backup: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
    
    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }
    
    private void copyDirectory(File src, File dest) throws Exception {
        if (!dest.exists()) {
            dest.mkdirs();
        }
        File[] files = src.listFiles();
        if (files != null) {
            for (File f : files) {
                File destFile = new File(dest, f.getName());
                if (f.isDirectory()) {
                    copyDirectory(f, destFile);
                } else {
                    Files.copy(f.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }
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
