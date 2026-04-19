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

import org.example.Model.BooksModel;
import org.example.Service.BookService;

public class BooksPanel extends JPanel {

   private BookService bookService;
   private DefaultTableModel bookTableModel;
   private JTable bookTable;
   private JTextField idField;
   private JTextField isbnField;
   private JTextField titleField;
   private JTextField authorField;
   private JTextField priceField;
   private JTextField stockField;
   private int selectedBookId = -1;

   public BooksPanel() {
      bookService = new BookService();
      initializePanel();
   }

   private void initializePanel() {
      setLayout(new GridBagLayout());
      setBackground(new Color(240, 248, 255));

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(10, 10, 10, 10);
      gbc.anchor = GridBagConstraints.WEST;

      JLabel titleLabel = new JLabel("Book Management");
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

      // ISBN Field
      gbc.gridx = 0;
      gbc.gridy = 2;
      add(new JLabel("ISBN:"), gbc);
      gbc.gridx = 1;
      isbnField = new JTextField(20);
      add(isbnField, gbc);

      // Title Field
      gbc.gridx = 0;
      gbc.gridy = 3;
      add(new JLabel("Title:"), gbc);
      gbc.gridx = 1;
      titleField = new JTextField(20);
      add(titleField, gbc);

      // Author Field
      gbc.gridx = 0;
      gbc.gridy = 4;
      add(new JLabel("Author:"), gbc);
      gbc.gridx = 1;
      authorField = new JTextField(20);
      add(authorField, gbc);

      // Price Field
      gbc.gridx = 0;
      gbc.gridy = 5;
      add(new JLabel("Price:"), gbc);
      gbc.gridx = 1;
      priceField = new JTextField(20);
      add(priceField, gbc);

      // Stock Field
      gbc.gridx = 0;
      gbc.gridy = 6;
      add(new JLabel("Stock:"), gbc);
      gbc.gridx = 1;
      stockField = new JTextField(20);
      add(stockField, gbc);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
      buttonPanel.setBackground(new Color(240, 248, 255));

      JButton addButton = new JButton("Add");
      JButton updateButton = new JButton("Update");
      JButton deleteButton = new JButton("Delete");
      JButton clearButton = new JButton("Clear");

      addButton.setPreferredSize(new Dimension(80, 30));
      updateButton.setPreferredSize(new Dimension(80, 30));
      deleteButton.setPreferredSize(new Dimension(80, 30));
      clearButton.setPreferredSize(new Dimension(80, 30));

      buttonPanel.add(addButton);
      buttonPanel.add(updateButton);
      buttonPanel.add(deleteButton);
      buttonPanel.add(clearButton);

      gbc.gridx = 0;
      gbc.gridy = 7;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.CENTER;
      add(buttonPanel, gbc);

      String[] columnNames = { "ID", "ISBN", "Title", "Author", "Price", "Stock" };
      bookTableModel = new DefaultTableModel(columnNames, 0) {
         @Override
         public boolean isCellEditable(int row, int column) {
            return false;
         }
      };

      bookTable = new JTable(bookTableModel);
      bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      JScrollPane tableScrollPane = new JScrollPane(bookTable);
      tableScrollPane.setPreferredSize(new Dimension(700, 200));

      gbc.gridy = 8;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      add(tableScrollPane, gbc);

      // Button Actions
      addButton.addActionListener(e -> addBook());
      updateButton.addActionListener(e -> updateBook());
      deleteButton.addActionListener(e -> deleteBook());
      clearButton.addActionListener(e -> clearFields());

      // Table selection listener
      bookTable.getSelectionModel().addListSelectionListener(e -> {
         if (!e.getValueIsAdjusting()) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
               selectedBookId = (int) bookTableModel.getValueAt(selectedRow, 0);
               idField.setText(String.valueOf(selectedBookId));
               isbnField.setText(bookTableModel.getValueAt(selectedRow, 1).toString());
               titleField.setText(bookTableModel.getValueAt(selectedRow, 2).toString());
               authorField.setText(bookTableModel.getValueAt(selectedRow, 3).toString());
               priceField.setText(bookTableModel.getValueAt(selectedRow, 4).toString());
               stockField.setText(bookTableModel.getValueAt(selectedRow, 5).toString());
            }
         }
      });

      loadBooks();
   }

   private void addBook() {
      try {
         String isbn = isbnField.getText().trim();
         String title = titleField.getText().trim();
         String author = authorField.getText().trim();
         String priceText = priceField.getText().trim();
         String stockText = stockField.getText().trim();

         if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
         }

         double price = Double.parseDouble(priceText);
         int stock = Integer.parseInt(stockText);

         BooksModel book = new BooksModel();
         book.setIsbn(isbn);
         book.setTitle(title);
         book.setAuthor(author);
         book.setPrice(price);
         book.setStock(stock);

         boolean success = bookService.addBook(book);
         if (success) {
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            loadBooks();
            clearFields();
         } else {
            JOptionPane.showMessageDialog(this, "Failed to add book. ISBN may already exist.");
         }

      } catch (NumberFormatException ex) {
         JOptionPane.showMessageDialog(this, "Invalid number format!");
      } catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
      }
   }

   private void updateBook() {
      try {
         if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to update!");
            return;
         }

         String title = titleField.getText().trim();
         String author = authorField.getText().trim();
         String priceText = priceField.getText().trim();
         String stockText = stockField.getText().trim();

         if (title.isEmpty() || author.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
         }

         double price = Double.parseDouble(priceText);
         int stock = Integer.parseInt(stockText);

         boolean success = bookService.updateBook(selectedBookId, title, author, price, stock);
         if (success) {
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            loadBooks();
            clearFields();
         } else {
            JOptionPane.showMessageDialog(this, "Failed to update book.");
         }

      } catch (NumberFormatException ex) {
         JOptionPane.showMessageDialog(this, "Invalid number format!");
      } catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
      }
   }

   private void deleteBook() {
      int selectedRow = bookTable.getSelectedRow();
      if (selectedRow >= 0) {
         int confirm = JOptionPane.showConfirmDialog(this, "Delete this book?");
         if (confirm == JOptionPane.YES_OPTION) {
            try {
               boolean success = bookService.deleteBook(selectedBookId);
               if (success) {
                  JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                  loadBooks();
                  clearFields();
               } else {
                  JOptionPane.showMessageDialog(this, "Failed to delete book. It may have active borrows.");
               }
            } catch (Exception ex) {
               JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
         }
      } else {
         JOptionPane.showMessageDialog(this, "Please select a book to delete!");
      }
   }

   private void loadBooks() {
      bookTableModel.setRowCount(0);
      try {
         var books = bookService.getAllBooks();
         for (BooksModel book : books) {
            bookTableModel.addRow(new Object[] {
                  book.getId(),
                  book.getIsbn(),
                  book.getTitle(),
                  book.getAuthor(),
                  book.getPrice(),
                  book.getStock()
            });
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
      }
   }

   private void clearFields() {
      idField.setText("");
      isbnField.setText("");
      titleField.setText("");
      authorField.setText("");
      priceField.setText("");
      stockField.setText("");
      selectedBookId = -1;
      bookTable.clearSelection();
   }
}
