package org.example.GUI.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class AboutPanel extends JPanel {
   private static final String FONT_NAME = "Arial";

   public AboutPanel() {
      setLayout(new BorderLayout());
      setBackground(new Color(255, 250, 240));

      JLabel titleLabel = new JLabel("About This Application", SwingConstants.CENTER);
      titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 20));
      titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      JTextArea aboutText = new JTextArea();
      aboutText.setText("\n\nLibrary Management System\n\n"
            + "Version: 1.0.0\n\n"
            + "This application manages library operations.\n\n"
            + "Features:\n"
            + "• Book Inventory Management\n"
            + "• Borrow & Return System\n"
            + "• Member Management\n"
            + "• Fine Tracking\n\n"
            + "Created with Java Swing.\n\n"
            + "© 2026 - All rights reserved");
      aboutText.setEditable(false);
      aboutText.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
      aboutText.setLineWrap(true);
      aboutText.setWrapStyleWord(true);
      aboutText.setMargin(new Insets(10, 30, 10, 30));
      aboutText.setBackground(new Color(255, 250, 240));

      add(titleLabel, BorderLayout.NORTH);
      add(aboutText, BorderLayout.CENTER);
   }
}
