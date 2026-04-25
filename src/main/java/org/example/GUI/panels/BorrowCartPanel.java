package org.example.GUI.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.Model.MemberModel;
import org.example.Service.BookService;
import org.example.Service.BorrowService;
import org.example.Service.MemberService;

public class BorrowCartPanel extends JPanel {
    private JComboBox<BooksModel> bookComboBox;
    private JComboBox<MemberModel> memberComboBox;
    private JSpinner quantitySpinner;
    private JButton addToCartButton;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalItemsLabel;
    private JButton confirmBorrowButton;
    private JButton clearCartButton;
    private JTextField slipNoField;

    private BookService bookService;
    private BorrowService borrowService;
    private MemberService memberService;

    private final Consumer<BorrowSlipsModel> onBorrowConfirmed;

    public BorrowCartPanel(Consumer<BorrowSlipsModel> onBorrowConfirmed) {
        this.onBorrowConfirmed = onBorrowConfirmed;
        bookService = new BookService();
        borrowService = new BorrowService();
        memberService = new MemberService();

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
        layoutComponents();
        loadBooks();
        loadMembers();
    }

    private void initializeComponents() {
        slipNoField = new JTextField("SLIP-" + System.currentTimeMillis());
        slipNoField.setEditable(false);
        slipNoField.setFont(new Font("Arial", Font.BOLD, 14));
        slipNoField.setPreferredSize(new Dimension(200, 30));

        memberComboBox = new JComboBox<>();
        memberComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        memberComboBox.setPreferredSize(new Dimension(200, 30));
        memberComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MemberModel) {
                    MemberModel member = (MemberModel) value;
                    setText(member.getName() + " (" + member.getStatus() + ")");
                }
                return this;
            }
        });

        bookComboBox = new JComboBox<>();
        bookComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        bookComboBox.setPreferredSize(new Dimension(250, 30));
        bookComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BooksModel) {
                    BooksModel book = (BooksModel) value;
                    setText(book.getTitle() + " - Stock: " + book.getStock());
                }
                return this;
            }
        });

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) quantitySpinner.getEditor()).getTextField().setColumns(5);

        addToCartButton = new JButton("Add to Cart");
        addToCartButton.setFont(new Font("Arial", Font.BOLD, 14));
        addToCartButton.setPreferredSize(new Dimension(120, 30));
        addToCartButton.addActionListener(e -> addToCart());

        String[] columnNames = { "Book Title", "ISBN", "Author", "Quantity" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(35);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(240, 240, 240));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < cartTable.getColumnCount(); i++) {
            cartTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        totalItemsLabel = new JLabel("0 items");
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        confirmBorrowButton = new JButton("Confirm Borrow");
        confirmBorrowButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmBorrowButton.setPreferredSize(new Dimension(170, 40));
        confirmBorrowButton.addActionListener(e -> confirmBorrow());

        clearCartButton = new JButton("Clear Cart");
        clearCartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearCartButton.setPreferredSize(new Dimension(120, 40));
        clearCartButton.addActionListener(e -> clearCart());
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Create Borrow Slip", 0, 0,
                        new Font("Arial", Font.BOLD, 18)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel slipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        slipPanel.setBackground(Color.WHITE);
        slipPanel.add(new JLabel("Slip No:"));
        slipPanel.add(slipNoField);
        slipPanel.add(Box.createHorizontalStrut(20));
        slipPanel.add(new JLabel("Date: " + LocalDate.now()));

        JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        memberPanel.setBackground(Color.WHITE);
        memberPanel.add(new JLabel("Member:"));
        memberPanel.add(memberComboBox);

        JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bookPanel.setBackground(Color.WHITE);
        bookPanel.add(new JLabel("Book:"));
        bookPanel.add(bookComboBox);
        bookPanel.add(Box.createHorizontalStrut(10));
        bookPanel.add(new JLabel("Quantity:"));
        bookPanel.add(quantitySpinner);
        bookPanel.add(Box.createHorizontalStrut(10));
        bookPanel.add(addToCartButton);

        topPanel.add(slipPanel);
        topPanel.add(memberPanel);
        topPanel.add(bookPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Borrow Items", 0, 0,
                        new Font("Arial", Font.BOLD, 16)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setPreferredSize(new Dimension(650, 250));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        JLabel totalTitleLabel = new JLabel("Total Items");
        totalTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalPanel.add(totalTitleLabel, BorderLayout.WEST);
        totalPanel.add(totalItemsLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearCartButton);
        buttonPanel.add(confirmBorrowButton);

        bottomPanel.add(totalPanel);
        bottomPanel.add(buttonPanel);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadBooks() {
        try {
            var books = bookService.getAllBooks();
            bookComboBox.removeAllItems();
            for (BooksModel book : books) bookComboBox.addItem(book);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMembers() {
        try {
            var members = memberService.getAllMembers();
            memberComboBox.removeAllItems();
            for (MemberModel member : members) memberComboBox.addItem(member);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToCart() {
        BooksModel selectedBook = (BooksModel) bookComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();

        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select a book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = borrowService.addToCart(selectedBook.getId(), quantity);
        if (success) {
            updateCartTable();
            quantitySpinner.setValue(1);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add to cart. Check stock availability.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);
        List<BorrowItemsModel> cart = borrowService.getCart();
        for (BorrowItemsModel item : cart) {
            tableModel.addRow(new Object[]{
                    item.getBook().getTitle(),
                    item.getBook().getIsbn(),
                    item.getBook().getAuthor(),
                    item.getQuantity()
            });
        }
        updateTotals();
    }

    private void updateTotals() {
        List<BorrowItemsModel> cart = borrowService.getCart();
        int totalItems = cart.stream().mapToInt(BorrowItemsModel::getQuantity).sum();
        totalItemsLabel.setText(totalItems + " items");
    }

    private void clearCart() {
        if (borrowService.getCart().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all items from cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            borrowService.clearCart();
            updateCartTable();
        }
    }

    private void confirmBorrow() {
        List<BorrowItemsModel> cart = borrowService.getCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Borrow", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MemberModel selectedMember = (MemberModel) memberComboBox.getSelectedItem();
        if (selectedMember == null) {
            JOptionPane.showMessageDialog(this, "Please select a member", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("Borrow Summary:\n\nMember: ")
                .append(selectedMember.getName()).append("\n\n");
        for (BorrowItemsModel item : cart) {
            message.append(String.format("%s x%d\n", item.getBook().getTitle(), item.getQuantity()));
        }
        message.append(String.format("\nTotal Items: %d", cart.stream().mapToInt(BorrowItemsModel::getQuantity).sum()));

        int result = JOptionPane.showConfirmDialog(this, message.toString(), "Confirm Borrow", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        BorrowSlipsModel slip = borrowService.confirmBorrow(selectedMember.getId());
        if (slip != null) {
            onBorrowConfirmed.accept(slip);
            updateCartTable();
            slipNoField.setText("SLIP-" + System.currentTimeMillis());
            JOptionPane.showMessageDialog(this, "Borrow confirmed! Slip: " + slip.getSlipNo());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to confirm borrow.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}