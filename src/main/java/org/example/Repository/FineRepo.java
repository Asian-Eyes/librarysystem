package org.example.Repository;

import org.example.Model.QFinesModel;
import org.example.util.DB;
import org.hibernate.Session;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class FineRepo {

    private static final QFinesModel qFine = QFinesModel.finesModel;

    public double getUnpaidFineTotal(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Double result = new JPAQueryFactory(session)
                    .select(qFine.amount.sum().coalesce(0.0))
                    .from(qFine)
                    .where(
                            qFine.memberId.eq(memberId),
                            qFine.paid.isFalse()
                    )
                    .fetchOne();

            return result != null ? result : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
