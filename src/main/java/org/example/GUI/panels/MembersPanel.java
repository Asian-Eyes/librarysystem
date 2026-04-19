package org.example.GUI.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.example.Model.MemberModel;
import org.example.Service.MemberService;

public class MembersPanel extends JPanel {

    private MemberService memberService;
    private DefaultTableModel memberTableModel;
    private JTable memberTable;
    private JTextField idField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField statusField;
    private int selectedMemberId = -1;

    public MembersPanel() {
        memberService = new MemberService();
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Member Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // ID Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setEditable(false);
        add(idField, gbc);

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        // Contact Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        add(contactField, gbc);

        // Status Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusField = new JTextField(20);
        statusField.setEditable(false);
        add(statusField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton blockButton = new JButton("Block");
        JButton unblockButton = new JButton("Unblock");
        JButton clearButton = new JButton("Clear");

        addButton.setPreferredSize(new Dimension(80, 30));
        updateButton.setPreferredSize(new Dimension(80, 30));
        blockButton.setPreferredSize(new Dimension(80, 30));
        unblockButton.setPreferredSize(new Dimension(90, 30));
        clearButton.setPreferredSize(new Dimension(80, 30));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(unblockButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        String[] columnNames = { "ID", "Name", "Contact", "Status" };
        memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(memberTableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(memberTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(tableScrollPane, gbc);

        // Button Actions
        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        blockButton.addActionListener(e -> blockMember());
        unblockButton.addActionListener(e -> unblockMember());
        clearButton.addActionListener(e -> clearFields());

        // Table selection listener
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedMemberId = (int) memberTableModel.getValueAt(selectedRow, 0);
                    idField.setText(String.valueOf(selectedMemberId));
                    nameField.setText(memberTableModel.getValueAt(selectedRow, 1).toString());
                    contactField.setText(memberTableModel.getValueAt(selectedRow, 2).toString());
                    statusField.setText(memberTableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        loadMembers();
    }

    private void addMember() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Name and Contact!");
                return;
            }

            MemberModel member = new MemberModel();
            member.setName(name);
            member.setContact(contact);

            boolean success = memberService.createMember(member);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member added successfully!");
                loadMembers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add member.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateMember() {
        try {
            if (selectedMemberId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a member to update!");
                return;
            }

            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Name and Contact!");
                return;
            }

            boolean success = memberService.updateMember(selectedMemberId, name, contact);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member updated successfully!");
                loadMembers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update member.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void blockMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to block!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Block this member?");
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = memberService.blockMember(selectedMemberId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member blocked successfully!");
                loadMembers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to block member.");
            }
        }
    }

    private void unblockMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to unblock!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Unblock this member?");
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = memberService.unblockMember(selectedMemberId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Member unblocked successfully!");
                loadMembers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unblock member.");
            }
        }
    }

    private void loadMembers() {
        memberTableModel.setRowCount(0);
        try {
            var members = memberService.getAllMembers();
            for (MemberModel member : members) {
                memberTableModel.addRow(new Object[] {
                        member.getId(),
                        member.getName(),
                        member.getContact(),
                        member.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        statusField.setText("");
        selectedMemberId = -1;
        memberTable.clearSelection();
    }
}
