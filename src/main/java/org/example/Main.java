package org.example;

import javax.swing.SwingUtilities;
import org.example.GUI.AuthWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuthWindow::new);
    }
}