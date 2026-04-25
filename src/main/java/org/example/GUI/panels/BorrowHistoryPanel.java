package org.example.GUI.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;

public class BorrowHistoryPanel extends JPanel {

    private DefaultTableModel tableModel;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public BorrowHistoryPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Borrow History");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        String[] columns = { "Slip No", "Member", "Book Title", "Qty", "Borrowed At", "Due At", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);
    }

    public void addSlip(BorrowSlipsModel slip) {
        for (BorrowItemsModel item : slip.getItems()) {
            tableModel.addRow(new Object[]{
                    slip.getSlipNo(),
                    slip.getMember().getName(),
                    item.getBook().getTitle(),
                    item.getQuantity(),
                    slip.getBorrowAt().format(FMT),
                    slip.getDueAt().format(FMT),
                    slip.getStatus()
            });
        }
    }
}