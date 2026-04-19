package org.example.Repository;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BorrowSlipRepo {

    public List<BorrowSlipsModel> getActiveSlipsByMember(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM BorrowSlipsModel bs WHERE bs.member.id = :memberId AND bs.status = :status",
                            BorrowSlipsModel.class)
                    .setParameter("memberId", memberId)
                    .setParameter("status", "ACTIVE")
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Failed to fetch active slips: " + e.getMessage());
            return List.of();
        }
    }

    public boolean returnItems(int slipId, int bookId, int returnQty) {
        if (returnQty <= 0) {
            System.err.println("Return quantity must be greater than 0.");
            return false;
        }
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            BorrowSlipsModel slip = session.find(BorrowSlipsModel.class, slipId);
            if (slip == null) {
                System.err.println("Borrow slip not found.");
                tx.rollback();
                return false;
            }
            if ("RETURNED".equalsIgnoreCase(slip.getStatus())) {
                System.err.println("This slip is already fully returned.");
                tx.rollback();
                return false;
            }

            BorrowItemsModel targetItem = null;
            for (BorrowItemsModel item : slip.getItems()) {
                if (item.getBook().getId() == bookId) {
                    targetItem = item;
                    break;
                }
            }
            if (targetItem == null) {
                System.err.println("Book not found in this borrow slip.");
                tx.rollback();
                return false;
            }

            int remaining = targetItem.getQuantity() - targetItem.getReturnedQty();
            if (returnQty > remaining) {
                System.err.println("Return quantity exceeds borrowed amount. Remaining: " + remaining);
                tx.rollback();
                return false;
            }

            targetItem.setReturnedQty(targetItem.getReturnedQty() + returnQty);
            session.merge(targetItem);

            BooksModel book = session.find(BooksModel.class, bookId);
            book.setStock(book.getStock() + returnQty);
            session.merge(book);

            boolean allReturned = slip.getItems().stream()
                    .allMatch(i -> i.getReturnedQty() >= i.getQuantity());
            if (allReturned) {
                slip.setStatus("RETURNED");
                session.merge(slip);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to process return: " + e.getMessage());
            return false;
        }
    }

    public List<BorrowSlipsModel> getSlipsByStatus(String status) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM BorrowSlipsModel bs WHERE bs.status = :status",
                            BorrowSlipsModel.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Failed to fetch slips by status: " + e.getMessage());
            return List.of();
        }
    }
}
