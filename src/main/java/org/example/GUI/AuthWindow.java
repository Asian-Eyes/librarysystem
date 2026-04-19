package org.example.GUI;

import org.example.GUI.main.MainWindow;
import org.example.GUI.panels.AuthPanel;

import javax.swing.*;

public class AuthWindow extends JFrame {

    public AuthWindow() {
        setTitle("Library System");
        setSize(440, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(new AuthPanel(this::onSuccess));
        setVisible(true);
    }

    private void onSuccess() {
        dispose();
        new MainWindow();
    }
}