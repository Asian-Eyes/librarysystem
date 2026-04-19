package org.example.Service;

import java.util.List;

import org.example.Model.BooksModel;
import org.example.Repository.BookInventoryRepo;
import org.example.Repository.BorrowItemRepo;

public class BookService {
   private final BookInventoryRepo bookRepo = new BookInventoryRepo();
   private final BorrowItemRepo borrowItemRepo = new BorrowItemRepo();

   public boolean addBook(BooksModel book) {
      if (book == null || isBlank(book.getIsbn()) || isBlank(book.getTitle()) || isBlank(book.getAuthor())) {
         return false;
      }

      if (book.getPrice() < 0 || book.getStock() < 0) {
         return false;
      }

      if (bookRepo.findByIsbn(book.getIsbn()) != null) {
         return false;
      }

      return bookRepo.addBook(book);
   }

   public List<BooksModel> getAllBooks() {
      return bookRepo.getAllBooks();
   }

   public BooksModel getBookById(int id) {
      return bookRepo.getBookById(id);
   }

   public boolean updateBook(int id, String title, String author, double price, int stock) {
      if (id <= 0 || isBlank(title) || isBlank(author) || price < 0 || stock < 0) {
         return false;
      }

      return bookRepo.updateBook(id, title.trim(), author.trim(), price, stock);
   }

   public boolean deleteBook(int id) {
      if (id <= 0 || borrowItemRepo.hasActiveBorrows(id)) {
         return false;
      }

      return bookRepo.deleteBook(id);
   }

   public boolean deductStock(int bookId, int quantity) {
      BooksModel book = getBookById(bookId);
      if (book == null || quantity <= 0 || book.getStock() < quantity) {
         return false;
      }

      return bookRepo.updateBook(
            bookId,
            book.getTitle(),
            book.getAuthor(),
            book.getPrice(),
            book.getStock() - quantity);
   }

   private boolean isBlank(String value) {
      return value == null || value.trim().isEmpty();
   }
}
