package org.example.Repository;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.util.DB;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class BorrowCartRepo {

    private final List<BorrowItemsModel> cart = new ArrayList<>();

    public List<BooksModel> searchBooks(String keyword) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM BooksModel b WHERE LOWER(b.isbn) LIKE :kw OR LOWER(b.title) LIKE :kw OR LOWER(b.author) LIKE :kw",
                            BooksModel.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
            return List.of();
        }
    }

    public boolean addToCart(int bookId, int quantity) {
        try (Session session = DB.getSessionFactory().openSession()) {
            BooksModel book = session.find(BooksModel.class, bookId);
            if (book == null) {
                System.err.println("Book not found.");
                return false;
            }
            if (book.getStock() < quantity) {
                System.err.println("Not enough stock. Available: " + book.getStock());
                return false;
            }
            for (BorrowItemsModel item : cart) {
                if (item.getBookId() == bookId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return true;
                }
            }
            BorrowItemsModel item = new BorrowItemsModel();
            item.setBookId(bookId);
            item.setQuantity(quantity);
            item.setReturnedQty(0);
            cart.add(item);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add to cart: " + e.getMessage());
            return false;
        }
    }

    public List<BorrowItemsModel> getCart() {
        return cart;
    }
}