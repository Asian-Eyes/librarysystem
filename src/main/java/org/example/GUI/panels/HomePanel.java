package org.example.GUI.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class HomePanel extends JPanel {
    private static final String FONT_NAME = "Arial";

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Welcome to the Home Tab", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea textArea = new JTextArea();
        textArea.setText("This is the home panel.\n\nYou can add any content here such as:\n"
                + "- Dashboard widgets\n"
                + "- Recent activities\n"
                + "- Quick actions\n"
                + "- Statistics and charts");
        textArea.setEditable(false);
        textArea.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}
