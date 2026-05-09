package org.example.Repository;

import org.example.Model.FinesModel;
import org.example.Model.QFinesModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class FineRepo {

    private static final QFinesModel qFine = QFinesModel.finesModel;

    public boolean saveFine(FinesModel fine) {
        Session session = DB.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(fine);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to save fine: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    public List<FinesModel> getFinesByMember(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return new JPAQueryFactory(session)
                    .selectFrom(qFine)
                    .where(qFine.memberId.eq(memberId))
                    .orderBy(qFine.createdAt.desc())
                    .fetch();
        } catch (Exception e) {
            System.err.println("Failed to get fines: " + e.getMessage());
            return List.of();
        }
    }

    public List<FinesModel> getUnpaidFinesByMember(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            return new JPAQueryFactory(session)
                    .selectFrom(qFine)
                    .where(qFine.memberId.eq(memberId), qFine.paid.isFalse())
                    .orderBy(qFine.createdAt.desc())
                    .fetch();
        } catch (Exception e) {
            System.err.println("Failed to get unpaid fines: " + e.getMessage());
            return List.of();
        }
    }

    public double getUnpaidFineTotal(int memberId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Double result = new JPAQueryFactory(session)
                    .select(qFine.amount.sum().coalesce(0.0))
                    .from(qFine)
                    .where(qFine.memberId.eq(memberId), qFine.paid.isFalse())
                    .fetchOne();
            return result != null ? result : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public boolean fineExistsForSlip(int slipId) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Long count = new JPAQueryFactory(session)
                    .select(qFine.count())
                    .from(qFine)
                    .where(qFine.slipId.eq(slipId))
                    .fetchOne();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}