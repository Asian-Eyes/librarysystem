package org.example.Repository;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.Model.MemberModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorrowCartRepo {

    private static final int DUE_DAYS = 14;
    private final List<BorrowItemsModel> cart = new ArrayList<>();

    public List<BooksModel> searchBooks(String keyword) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return session.createQuery(
                            """
                            FROM BooksModel b
                            WHERE LOWER(b.isbn) LIKE :kw
                               OR LOWER(b.title) LIKE :kw
                               OR LOWER(b.author) LIKE :kw
                            """,
                            BooksModel.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
            return List.of();
        }
    }

    public boolean addToCart(int bookId, int quantity) {
        if (quantity <= 0) {
            System.err.println("Quantity must be greater than 0.");
            return false;
        }

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
                if (item.getBook().getId() == bookId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return true;
                }
            }

            BorrowItemsModel item = new BorrowItemsModel();
            item.setBook(book);
            item.setQuantity(quantity);
            item.setReturnedQty(0);

            cart.add(item);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to add to cart: " + e.getMessage());
            return false;
        }
    }

    public boolean confirmBorrow(int memberId) {

        if (cart.isEmpty()) {
            System.err.println("Cart is empty.");
            return false;
        }

        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            MemberModel member = session.find(MemberModel.class, memberId);

            if (member == null) {
                System.err.println("Member not found.");
                tx.rollback();
                return false;
            }

            if (!"ACTIVE".equalsIgnoreCase(member.getStatus())) {
                System.err.println("Member is not active.");
                tx.rollback();
                return false;
            }

            for (BorrowItemsModel item : cart) {
                BooksModel book = session.find(BooksModel.class, item.getBook().getId());

                if (book == null || book.getStock() < item.getQuantity()) {
                    System.err.println("Stock issue for book: " + item.getBook().getTitle());
                    tx.rollback();
                    return false;
                }
            }

            // ✅ Create borrow slip using relationship
            BorrowSlipsModel slip = new BorrowSlipsModel();
            slip.setSlipNo(UUID.randomUUID().toString());
            slip.setMember(member);
            slip.setBorrowAt(LocalDateTime.now());
            slip.setDueAt(LocalDateTime.now().plusDays(DUE_DAYS));
            slip.setStatus("ACTIVE");

            session.persist(slip);

            for (BorrowItemsModel item : cart) {

                BooksModel book = session.find(BooksModel.class, item.getBook().getId());

                book.setStock(book.getStock() - item.getQuantity());
                session.merge(book);

                item.setBorrowSlip(slip);
                item.setBook(book);

                session.persist(item);
            }

            tx.commit();

            cart.clear();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to confirm borrow: " + e.getMessage());
            return false;
        }
    }

    public List<BorrowItemsModel> getCart() {
        return cart;
    }
}