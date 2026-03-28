package org.example.Repository;

import org.example.Model.MemberModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MemberRepo {

    public boolean createMember(MemberModel member) {
        if (member.getStatus() == null || member.getStatus().isBlank()) {
            member.setStatus("ACTIVE");
        }

        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.persist(member);
            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to create member: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMember(int id, String name, String contact) {

        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {

            MemberModel member = session.find(MemberModel.class, id);

            if (member == null) {
                System.err.println("No member found with ID " + id);
                return false;
            }

            tx = session.beginTransaction();

            member.setName(name);
            member.setContact(contact);

            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to update member: " + e.getMessage());
            return false;
        }
    }

    public boolean blockMember(int id) {
        return setStatus(id, "BLOCKED");
    }

    public boolean unblockMember(int id) {
        return setStatus(id, "ACTIVE");
    }

    private boolean setStatus(int id, String status) {

        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {

            MemberModel member = session.find(MemberModel.class, id);

            if (member == null) {
                System.err.println("No member found with ID " + id);
                return false;
            }

            tx = session.beginTransaction();

            member.setStatus(status);

            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to set member status: " + e.getMessage());
            return false;
        }
    }

    public List<MemberModel> getAllMembers() {

        try (Session session = DB.getSessionFactory().openSession()) {

            return session.createQuery("FROM MemberModel", MemberModel.class)
                    .getResultList();

        } catch (Exception e) {
            System.err.println("Failed to retrieve members: " + e.getMessage());
            return List.of();
        }
    }

    public long getActiveBorrowCount(int memberId) {

        try (Session session = DB.getSessionFactory().openSession()) {

            String hql = """
                SELECT COUNT(bs)
                FROM BorrowSlipsModel bs
                WHERE bs.member.id = :memberId
                AND bs.status != :returned
            """;

            Long result = session.createQuery(hql, Long.class)
                    .setParameter("memberId", memberId)
                    .setParameter("returned", "RETURNED")
                    .uniqueResult();

            return result != null ? result : 0L;

        } catch (Exception e) {
            System.err.println("Failed to count active borrows: " + e.getMessage());
            return 0L;
        }
    }

    public double getUnpaidFineTotal(int memberId) {

        try (Session session = DB.getSessionFactory().openSession()) {

            String hql = """
                SELECT COALESCE(SUM(f.amount), 0.0)
                FROM FinesModel f
                WHERE f.member.id = :memberId
                AND f.paid = false
            """;

            Double result = session.createQuery(hql, Double.class)
                    .setParameter("memberId", memberId)
                    .uniqueResult();

            return result != null ? result : 0.0;

        } catch (Exception e) {
            System.err.println("Failed to sum unpaid fines: " + e.getMessage());
            return 0.0;
        }
    }
}