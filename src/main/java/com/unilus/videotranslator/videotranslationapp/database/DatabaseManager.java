/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/videotranslation_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";
    
    // Static block to load MySQL driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database!");
            System.err.println("Make sure MySQL is running and credentials are correct.");
            throw e;
        }
    }
    
    // Test connection
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return false;
        }
    }
    
    // Authenticate user
    public static boolean authenticateUser(String username, String password, String role) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ? AND password = ? AND role = ?")) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get user ID
    public static int getUserId(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Get full name
    public static String getFullName(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT fullname FROM users WHERE username = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("fullname");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
    
    // Save recording
    public static boolean saveRecording(int lecturerId, String title, String filename, 
                                       String transcriptFile, int duration, long fileSize) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO recordings (lecturer_id, title, filename, transcript_file, duration, file_size) VALUES (?, ?, ?, ?, ?, ?)")) {
            
            stmt.setInt(1, lecturerId);
            stmt.setString(2, title);
            stmt.setString(3, filename);
            stmt.setString(4, transcriptFile);
            stmt.setInt(5, duration);
            stmt.setLong(6, fileSize);
            
            stmt.executeUpdate();
            System.out.println("✅ Recording saved to database!");
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all recordings
    public static ResultSet getAllRecordings() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM recordings ORDER BY created_at DESC");
    }
    
    // Get recordings by lecturer
    public static ResultSet getRecordingsByLecturer(int lecturerId) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM recordings WHERE lecturer_id = ? ORDER BY created_at DESC");
        stmt.setInt(1, lecturerId);
        return stmt.executeQuery();
    }
    
    // Mark recording as viewed
    public static boolean markRecordingAsViewed(int studentId, int recordingId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO enrollments (student_id, recording_id, viewed_at) VALUES (?, ?, NOW()) ON DUPLICATE KEY UPDATE viewed_at = NOW()")) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, recordingId);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get total statistics
    public static class Statistics {
        public int totalLectures;
        public int totalStudents;
        public int totalLecturers;
        
        public Statistics(int lectures, int students, int lecturers) {
            this.totalLectures = lectures;
            this.totalStudents = students;
            this.totalLecturers = lecturers;
        }
    }
    
    public static Statistics getStatistics() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as count FROM recordings");
            int lectures = rs1.next() ? rs1.getInt("count") : 0;
            
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE role = 'Student'");
            int students = rs2.next() ? rs2.getInt("count") : 0;
            
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE role = 'Lecturer'");
            int lecturers = rs3.next() ? rs3.getInt("count") : 0;
            
            return new Statistics(lectures, students, lecturers);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return new Statistics(0, 0, 0);
        }
    }
}
