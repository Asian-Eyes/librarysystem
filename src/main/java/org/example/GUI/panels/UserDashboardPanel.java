package org.example.GUI.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class UserDashboardPanel extends JPanel {

    private final Runnable onLogout;

    public UserDashboardPanel(Runnable onLogout) {
        this.onLogout = onLogout;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0;

        JLabel titleLabel = new JLabel("User Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton browseBooksBtn   = new JButton("Browse Books");
        JButton borrowCartBtn    = new JButton("Borrow Cart");
        JButton borrowHistoryBtn = new JButton("Borrow History");
        JButton returnBtn        = new JButton("Return Books");
        JButton finesBtn         = new JButton("Fines");
        JButton logoutBtn        = new JButton("Logout");

        Dimension btnSize = new Dimension(130, 30);
        browseBooksBtn.setPreferredSize(btnSize);
        borrowCartBtn.setPreferredSize(btnSize);
        borrowHistoryBtn.setPreferredSize(btnSize);
        returnBtn.setPreferredSize(btnSize);
        finesBtn.setPreferredSize(btnSize);
        logoutBtn.setPreferredSize(btnSize);

        buttonPanel.add(browseBooksBtn);
        buttonPanel.add(borrowCartBtn);
        buttonPanel.add(borrowHistoryBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(finesBtn);
        buttonPanel.add(logoutBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(buttonPanel, gbc);

        browseBooksBtn.addActionListener(e -> switchTab("Book Management"));
        borrowCartBtn.addActionListener(e -> switchTab("Borrow Cart"));
        borrowHistoryBtn.addActionListener(e -> switchTab("Borrow History"));
        returnBtn.addActionListener(e -> switchTab("Return Books"));
        finesBtn.addActionListener(e -> switchTab("Fines"));
        logoutBtn.addActionListener(e -> onLogout.run());
    }

    private void switchTab(String tabTitle) {
        JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
        if (tabbedPane == null) return;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabTitle)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
    }
}