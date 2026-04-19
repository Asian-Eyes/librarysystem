package org.example.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.Model.BooksModel;
import org.example.Model.BorrowItemsModel;
import org.example.Model.BorrowSlipsModel;
import org.example.Model.MemberModel;
import org.example.Model.QBorrowSlipsModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class BorrowSlipRepo {

    private static final int DUE_DAYS = 14;
    private static final QBorrowSlipsModel qBorrowSlip = QBorrowSlipsModel.borrowSlipsModel;

    public long getActiveBorrowCount(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Long result = new JPAQueryFactory(session)
                    .select(qBorrowSlip.count())
                    .from(qBorrowSlip)
                    .where(
                            qBorrowSlip.member.id.eq(memberId),
                            qBorrowSlip.status.ne("RETURNED")
                    )
                    .fetchOne();

            return result != null ? result : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean createBorrowSlip(int memberId, List<BorrowItemsModel> cartItems) {
        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            MemberModel managedMember = session.find(MemberModel.class, memberId);
            if (managedMember == null) {
                tx.rollback();
                return false;
            }

            BorrowSlipsModel slip = new BorrowSlipsModel();
            slip.setSlipNo(UUID.randomUUID().toString());
            slip.setMember(managedMember);
            slip.setBorrowAt(LocalDateTime.now());
            slip.setDueAt(LocalDateTime.now().plusDays(DUE_DAYS));
            slip.setStatus("ACTIVE");
            session.persist(slip);

            for (BorrowItemsModel cartItem : cartItems) {
                BooksModel managedBook = session.find(BooksModel.class, cartItem.getBook().getId());
                if (managedBook == null || managedBook.getStock() < cartItem.getQuantity()) {
                    tx.rollback();
                    return false;
                }

                managedBook.setStock(managedBook.getStock() - cartItem.getQuantity());
                session.merge(managedBook);

                BorrowItemsModel borrowItem = new BorrowItemsModel();
                borrowItem.setBook(managedBook);
                borrowItem.setBorrowSlip(slip);
                borrowItem.setQuantity(cartItem.getQuantity());
                borrowItem.setReturnedQty(0);
                session.persist(borrowItem);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        }
    }
}
