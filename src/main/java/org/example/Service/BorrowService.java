package org.example.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.Model.MemberModel;
import org.example.Repository.BookInventoryRepo;
import org.example.Repository.BorrowCartRepo;
import org.example.Repository.BorrowSlipRepo;
import org.example.Repository.MemberRepo;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BorrowService {

    private static final int DUE_DAYS = 14;
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
        if (quantity <= 0) return false;

        BooksModel selectedBook = bookRepo.getBookById(bookId);
        if (selectedBook == null || selectedBook.getStock() < quantity) return false;

        for (BorrowItemsModel item : cart) {
            if (item.getBook().getId() == bookId) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > selectedBook.getStock()) return false;
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

    public BorrowSlipsModel confirmBorrow(int memberId) {
        if (cart.isEmpty()) return null;

        Session session = DB.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            MemberModel member = session.find(MemberModel.class, memberId);
            if (member == null || !"ACTIVE".equalsIgnoreCase(member.getStatus())) return null;

            for (BorrowItemsModel item : cart) {
                BooksModel book = session.find(BooksModel.class, item.getBook().getId());
                if (book == null || book.getStock() < item.getQuantity()) {
                    tx.rollback();
                    return null;
                }
            }

            BorrowSlipsModel slip = new BorrowSlipsModel();
            slip.setSlipNo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            slip.setMember(member);
            slip.setBorrowAt(LocalDateTime.now());
            slip.setDueAt(LocalDateTime.now().plusDays(DUE_DAYS));
            slip.setStatus("ACTIVE");
            slip.setItems(new ArrayList<>());
            session.persist(slip);
            session.flush();

            for (BorrowItemsModel item : cart) {
                BooksModel book = session.find(BooksModel.class, item.getBook().getId());
                book.setStock(book.getStock() - item.getQuantity());
                session.merge(book);

                BorrowItemsModel newItem = new BorrowItemsModel();
                newItem.setBorrowSlip(slip);
                newItem.setBook(book);
                newItem.setQuantity(item.getQuantity());
                newItem.setReturnedQty(0);
                session.persist(newItem);
                slip.getItems().add(newItem);
            }

            tx.commit();
            cart.clear();
            return slip;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Confirm borrow failed: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }
}