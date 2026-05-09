package org.example.GUI.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.Repository.BorrowSlipRepo;
import org.example.Service.FineService;

public class ReturnBooksPanel extends JPanel {

    private static final double FINE_PER_DAY = 5.0;

    private final BorrowSlipRepo borrowSlipRepo = new BorrowSlipRepo();
    private final FineService fineService = new FineService();

    private JTextField slipNoField;
    private JButton searchBtn;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JSpinner returnQtySpinner;
    private JButton returnBtn;
    private JLabel slipInfoLabel;

    private BorrowSlipsModel currentSlip;

    public ReturnBooksPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255));
        initializePanel();
    }

    private void initializePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Return Books");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Slip No:"), gbc);

        gbc.gridx = 1;
        slipNoField = new JTextField(20);
        add(slipNoField, gbc);

        gbc.gridx = 2;
        searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 28));
        add(searchBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        slipInfoLabel = new JLabel(" ");
        slipInfoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        add(slipInfoLabel, gbc);

        String[] columns = { "Book Title", "ISBN", "Qty Borrowed", "Qty Returned", "Remaining", "Fine (if late)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        itemsTable = new JTable(tableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        itemsTable.setRowHeight(24);
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(680, 200));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        add(scrollPane, gbc);

        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        returnPanel.setBackground(new Color(240, 248, 255));
        returnPanel.add(new JLabel("Return Qty:"));
        returnQtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        returnQtySpinner.setPreferredSize(new Dimension(60, 28));
        returnPanel.add(returnQtySpinner);
        returnBtn = new JButton("Return Selected");
        returnBtn.setPreferredSize(new Dimension(140, 28));
        returnPanel.add(returnBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(returnPanel, gbc);

        searchBtn.addActionListener(e -> searchSlip());
        returnBtn.addActionListener(e -> returnSelected());
    }

    private void searchSlip() {
        String slipNo = slipNoField.getText().trim();
        if (slipNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a slip number.");
            return;
        }

        BorrowSlipsModel slip = borrowSlipRepo.findBySlipNo(slipNo);
        if (slip == null) {
            JOptionPane.showMessageDialog(this, "No slip found with number: " + slipNo);
            currentSlip = null;
            tableModel.setRowCount(0);
            slipInfoLabel.setText(" ");
            return;
        }

        currentSlip = slip;
        long overdueDays = computeOverdueDays(slip);
        slipInfoLabel.setText("Member: " + slip.getMember().getName()
                + "   |   Due: " + slip.getDueAt().toLocalDate()
                + "   |   Status: " + slip.getStatus()
                + (overdueDays > 0 ? "   |   Overdue by: " + overdueDays + " day(s)" : ""));
        loadSlipItems(overdueDays);
    }

    private void loadSlipItems(long overdueDays) {
        tableModel.setRowCount(0);
        if (currentSlip == null) return;
        for (BorrowItemsModel item : currentSlip.getItems()) {
            int remaining = item.getQuantity() - item.getReturnedQty();
            double fine = overdueDays > 0 ? fineService.computeFine(overdueDays, remaining) : 0.0;
            tableModel.addRow(new Object[]{
                    item.getBook().getTitle(),
                    item.getBook().getIsbn(),
                    item.getQuantity(),
                    item.getReturnedQty(),
                    remaining,
                    fine > 0 ? String.format("%.2f", fine) : "-"
            });
        }
    }

    private void returnSelected() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to return.");
            return;
        }
        if (currentSlip == null) {
            JOptionPane.showMessageDialog(this, "No slip loaded.");
            return;
        }

        int returnQty = (Integer) returnQtySpinner.getValue();
        int remaining = (int) tableModel.getValueAt(selectedRow, 4);

        if (returnQty > remaining) {
            JOptionPane.showMessageDialog(this, "Return quantity exceeds remaining: " + remaining);
            return;
        }

        BorrowItemsModel item = currentSlip.getItems().get(selectedRow);
        long overdueDays = computeOverdueDays(currentSlip);
        double fine = fineService.computeFine(overdueDays, returnQty);

        StringBuilder msg = new StringBuilder();
        msg.append("Returning ").append(returnQty).append(" x ").append(item.getBook().getTitle());
        if (fine > 0) {
            msg.append(String.format("\n\nLate return fine: %.2f (%.0f days overdue x %.1f/day x %d qty)",
                    fine, (double) overdueDays, FINE_PER_DAY, returnQty));
        }

        int confirm = JOptionPane.showConfirmDialog(this, msg.toString(), "Confirm Return", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        boolean success = borrowSlipRepo.returnItems(currentSlip.getId(), item.getBook().getId(), returnQty);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to process return.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fine > 0) {
            fineService.createFine(
                    currentSlip.getId(),
                    currentSlip.getMember().getId(),
                    fine,
                    String.format("Late return: %s (%d days overdue)", item.getBook().getTitle(), overdueDays)
            );
        }

        JOptionPane.showMessageDialog(this, fine > 0
                ? String.format("Returned! Fine of %.2f has been recorded.", fine)
                : "Returned successfully!");

        BorrowSlipsModel updated = borrowSlipRepo.findBySlipNo(currentSlip.getSlipNo());
        currentSlip = updated;
        loadSlipItems(computeOverdueDays(currentSlip));
    }

    private long computeOverdueDays(BorrowSlipsModel slip) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(slip.getDueAt())) {
            return ChronoUnit.DAYS.between(slip.getDueAt(), now);
        }
        return 0;
    }
}