package org.example.Service;

import java.util.ArrayList;
import java.util.List;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.MemberModel;
import org.example.Repository.BookInventoryRepo;
import org.example.Repository.BorrowCartRepo;
import org.example.Repository.BorrowSlipRepo;
import org.example.Repository.MemberRepo;

public class BorrowService {

   private final List<BorrowItemsModel> cart = new ArrayList<>();
   private final BookInventoryRepo bookRepo = new BookInventoryRepo();
   private final BorrowCartRepo borrowRepo = new BorrowCartRepo();
   private final BorrowSlipRepo borrowSlipRepo = new BorrowSlipRepo();
   private final MemberRepo memberRepo = new MemberRepo();

   public List<BooksModel> searchBooks(String keyword) {
      return borrowRepo.searchBooks(keyword);
   }

   public List<BorrowItemsModel> getCart() {
      return cart;
   }

   public void clearCart() {
      cart.clear();
   }

   public boolean addToCart(int bookId, int quantity) {
      if (quantity <= 0) {
         return false;
      }

      BooksModel selectedBook = bookRepo.getBookById(bookId);
      if (selectedBook == null || selectedBook.getStock() < quantity) {
         return false;
      }

      for (BorrowItemsModel item : cart) {
         if (item.getBook().getId() == bookId) {
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > selectedBook.getStock()) {
               return false;
            }
            item.setQuantity(newQuantity);
            return true;
         }
      }

      BorrowItemsModel item = new BorrowItemsModel();
      item.setBook(selectedBook);
      item.setQuantity(quantity);
      item.setReturnedQty(0);
      cart.add(item);
      return true;
   }
}
