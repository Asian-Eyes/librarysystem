package org.example.GUI.main;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.example.GUI.panels.AboutPanel;
import org.example.GUI.panels.BooksPanel;
import org.example.GUI.panels.BorrowCartPanel;
import org.example.GUI.panels.HomePanel;
import org.example.GUI.panels.MembersPanel;
import org.example.GUI.panels.SettingsPanel;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();



        tabbedPane.addTab("Book Management", new BooksPanel());
        tabbedPane.addTab("Borrow Cart", new BorrowCartPanel());
        tabbedPane.addTab("Members", new MembersPanel());
        tabbedPane.addTab("Home", new HomePanel());
        tabbedPane.addTab("Settings", new SettingsPanel());
        tabbedPane.addTab("About", new AboutPanel());

        add(tabbedPane);

        setVisible(true);
    }


}