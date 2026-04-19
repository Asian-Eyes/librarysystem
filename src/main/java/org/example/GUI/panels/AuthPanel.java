package org.example.GUI.panels;

import org.example.Model.UserModel;
import org.example.Repository.AuthRepo;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class AuthPanel extends JPanel {

    private final AuthRepo authRepo = new AuthRepo();
    private final Runnable onSuccess;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JTextField loginInputField;
    private JPasswordField loginPasswordField;

    private JTextField signupUsernameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;

    public AuthPanel(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(240, 248, 255));

        cardPanel.add(buildLoginPanel(), "LOGIN");
        cardPanel.add(buildSignupPanel(), "SIGNUP");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username or Email:"), gbc);
        gbc.gridx = 1;
        loginInputField = new JTextField(20);
        panel.add(loginInputField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(20);
        panel.add(loginPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton loginBtn = new JButton("Login");
        JButton toSignupBtn = new JButton("Create Account");
        loginBtn.setPreferredSize(new Dimension(120, 30));
        toSignupBtn.setPreferredSize(new Dimension(140, 30));
        buttonPanel.add(loginBtn);
        buttonPanel.add(toSignupBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        toSignupBtn.addActionListener(e -> cardLayout.show(cardPanel, "SIGNUP"));

        return panel;
    }

    private JPanel buildSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        signupUsernameField = new JTextField(20);
        panel.add(signupUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        signupEmailField = new JTextField(20);
        panel.add(signupEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        signupPasswordField = new JPasswordField(20);
        panel.add(signupPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        signupConfirmPasswordField = new JPasswordField(20);
        panel.add(signupConfirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton signupBtn = new JButton("Sign Up");
        JButton toLoginBtn = new JButton("Back to Login");
        signupBtn.setPreferredSize(new Dimension(100, 30));
        toLoginBtn.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(signupBtn);
        buttonPanel.add(toLoginBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        signupBtn.addActionListener(e -> handleSignup());
        toLoginBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));

        return panel;
    }

    private void handleLogin() {
        String input    = loginInputField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (input.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        UserModel user = authRepo.findByUsernameOrEmail(input);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            JOptionPane.showMessageDialog(this, "Invalid username/email or password.");
            return;
        }

        onSuccess.run();
    }

    private void handleSignup() {
        String username = signupUsernameField.getText().trim();
        String email    = signupEmailField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirm  = new String(signupConfirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }
        if (authRepo.existsByUsername(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken.");
            return;
        }
        if (authRepo.existsByEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered.");
            return;
        }

        UserModel user = new UserModel();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        boolean success = authRepo.createUser(user);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account created! You can now log in.");
            clearSignupFields();
            cardLayout.show(cardPanel, "LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create account. Please try again.");
        }
    }

    private void clearSignupFields() {
        signupUsernameField.setText("");
        signupEmailField.setText("");
        signupPasswordField.setText("");
        signupConfirmPasswordField.setText("");
    }
}