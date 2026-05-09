package org.example.Repository;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BorrowSlipRepo {

    public BorrowSlipsModel findBySlipNo(String slipNo) {
        try (Session session = DB.getSessionFactory().openSession()) {
            BorrowSlipsModel slip = session.createSelectionQuery(
                            "SELECT bs FROM BorrowSlipsModel bs " +
                                    "LEFT JOIN FETCH bs.member " +
                                    "LEFT JOIN FETCH bs.items i " +
                                    "LEFT JOIN FETCH i.book " +
                                    "WHERE bs.slipNo = :slipNo",
                            BorrowSlipsModel.class)
                    .setParameter("slipNo", slipNo)
                    .uniqueResult();
            return slip;
        } catch (Exception e) {
            System.err.println("Failed to find slip: " + e.getMessage());
            return null;
        }
    }

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
        if (returnQty <= 0) return false;
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            BorrowSlipsModel slip = session.createSelectionQuery(
                            "SELECT bs FROM BorrowSlipsModel bs " +
                                    "LEFT JOIN FETCH bs.items i " +
                                    "LEFT JOIN FETCH i.book " +
                                    "WHERE bs.id = :id",
                            BorrowSlipsModel.class)
                    .setParameter("id", slipId)
                    .uniqueResult();

            if (slip == null || "RETURNED".equalsIgnoreCase(slip.getStatus())) {
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
            if (targetItem == null) { tx.rollback(); return false; }

            int remaining = targetItem.getQuantity() - targetItem.getReturnedQty();
            if (returnQty > remaining) { tx.rollback(); return false; }

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