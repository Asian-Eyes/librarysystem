package org.example.GUI.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SettingsPanel extends JPanel {
    private static final String FONT_NAME = "Arial";

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(new Color(245, 245, 245));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JCheckBox darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        JCheckBox notificationsCheckBox = new JCheckBox("Enable Notifications");
        JCheckBox autoSaveCheckBox = new JCheckBox("Enable Auto-Save");

        darkModeCheckBox.setBackground(new Color(245, 245, 245));
        notificationsCheckBox.setBackground(new Color(245, 245, 245));
        autoSaveCheckBox.setBackground(new Color(245, 245, 245));

        optionsPanel.add(darkModeCheckBox);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(notificationsCheckBox);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(autoSaveCheckBox);
        optionsPanel.add(Box.createVerticalStrut(20));

        JButton applyButton = new JButton("Apply Settings");
        applyButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings applied!"));
        optionsPanel.add(applyButton);

        add(titleLabel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
    }
}
