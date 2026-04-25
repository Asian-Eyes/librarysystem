package org.example.GUI.main;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.example.GUI.AuthWindow;
import org.example.GUI.panels.AboutPanel;
import org.example.GUI.panels.BooksPanel;
import org.example.GUI.panels.BorrowCartPanel;
import org.example.GUI.panels.BorrowHistoryPanel;
import org.example.GUI.panels.FinesPanel;
import org.example.GUI.panels.HomePanel;
import org.example.GUI.panels.MembersPanel;
import org.example.GUI.panels.ReturnBooksPanel;
import org.example.GUI.panels.SettingsPanel;
import org.example.GUI.panels.UserDashboardPanel;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        BorrowHistoryPanel historyPanel = new BorrowHistoryPanel();
        BorrowCartPanel borrowCartPanel = new BorrowCartPanel(slip -> historyPanel.addSlip(slip));

        tabbedPane.addTab("Book Management", new BooksPanel());
        tabbedPane.addTab("Borrow Cart", borrowCartPanel);
        tabbedPane.addTab("Members", new MembersPanel());
        tabbedPane.addTab("Borrow History", historyPanel);
        tabbedPane.addTab("Return Books", new ReturnBooksPanel());
        tabbedPane.addTab("Fines", new FinesPanel());
        tabbedPane.addTab("Home", new HomePanel());
        tabbedPane.addTab("Settings", new SettingsPanel());
        tabbedPane.addTab("About", new AboutPanel());
        tabbedPane.addTab("Dashboard", new UserDashboardPanel(() -> {
            dispose();
            new AuthWindow();
        }));

        add(tabbedPane);
        setVisible(true);
    }
}