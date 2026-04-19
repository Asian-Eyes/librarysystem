package org.example.Repository;

import org.example.Model.QBorrowItemsModel;
import org.example.util.DB;
import org.hibernate.Session;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class BorrowItemRepo {

    private static final QBorrowItemsModel qBorrowItem = QBorrowItemsModel.borrowItemsModel;

    public boolean hasActiveBorrows(int bookId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Long count = new JPAQueryFactory(session)
                    .select(qBorrowItem.count())
                    .from(qBorrowItem)
                    .where(
                            qBorrowItem.book.id.eq(bookId),
                            qBorrowItem.borrowSlip.status.ne("RETURNED")
                    )
                    .fetchOne();

            return count != null && count > 0;
        } catch (Exception e) {
            return true;
        }
    }
}
