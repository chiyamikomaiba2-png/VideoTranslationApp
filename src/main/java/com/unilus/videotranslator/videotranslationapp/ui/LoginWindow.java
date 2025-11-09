package com.unilus.videotranslator.videotranslationapp.ui;

import com.unilus.videotranslator.videotranslationapp.database.DatabaseManager;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    
    public LoginWindow() {
        setTitle("Video Translation System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        Color bgColor = new Color(240, 240, 245);
        Color panelColor = Color.WHITE;
        Color accentColor = new Color(70, 130, 180);
        
        getContentPane().setBackground(bgColor);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(panelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Video Translation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("University of Lusaka");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(roleLabel, gbc);
        
        String[] roles = {"Student", "Lecturer", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(roleComboBox, gbc);
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(accentColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(loginButton, gbc);
        
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (authenticateUser(username, password, role)) {
            this.dispose();
            openRoleSpecificUI(role, username);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    private boolean authenticateUser(String username, String password, String role) {
        return DatabaseManager.authenticateUser(username, password, role);
    }
    
    private void openRoleSpecificUI(String role, String username) {
        int userId = DatabaseManager.getUserId(username);
        String fullName = DatabaseManager.getFullName(username);
        
        switch (role) {
            case "Student":
                new StudentWindow(username, userId, fullName);
                break;
            case "Lecturer":
                new LecturerWindow(username, userId, fullName);
                break;
            case "Admin":
                new AdminWindow(username, userId, fullName);
                break;
        }
    }
}
