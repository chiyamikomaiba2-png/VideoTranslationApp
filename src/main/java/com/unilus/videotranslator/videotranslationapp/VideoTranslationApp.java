package com.unilus.videotranslator.videotranslationapp;

import com.unilus.videotranslator.videotranslationapp.database.DatabaseManager;
import com.unilus.videotranslator.videotranslationapp.ui.LoginWindow;
import nu.pattern.OpenCV;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class VideoTranslationApp {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        
        System.out.println("=".repeat(50));
        System.out.println("🎬 VIDEO TRANSLATION APPLICATION");
        System.out.println("University of Lusaka - Final Year Project");
        System.out.println("=".repeat(50));
        
        // Test database connection
        System.out.println("\n📊 Testing database connection...");
        if (!DatabaseManager.testConnection()) {
            JOptionPane.showMessageDialog(null,
                "❌ DATABASE CONNECTION FAILED!\n\n" +
                "Make sure MySQL is running with:\n" +
                "- Host: localhost\n" +
                "- Port: 3306\n" +
                "- User: root\n" +
                "- Password: root123\n" +
                "- Database: videotranslation_db\n\n" +
                "Quick Fix:\n" +
                "1. Start MySQL: mysql -u root -p\n" +
                "2. Run the SQL script to create database\n" +
                "3. Restart this application",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        System.out.println("✅ Database connected successfully!");
        System.out.println("\n🚀 Launching application...\n");
        
        SwingUtilities.invokeLater(() -> {
            new LoginWindow();
        });
    }
}
