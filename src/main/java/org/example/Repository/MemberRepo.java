package org.example.Repository;

import java.util.List;

import org.example.Model.MemberModel;
import org.example.Model.QMemberModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class MemberRepo {

   private static final QMemberModel qMember = QMemberModel.memberModel;

   public boolean createMember(MemberModel member) {
      Transaction tx = null;

      try (Session session = DB.getSessionFactory().openSession()) {
         tx = session.beginTransaction();
         session.persist(member);
         tx.commit();
         return true;
      } catch (Exception e) {
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Failed to create member: " + e.getMessage());
         return false;
      }
   }

   public MemberModel getMemberById(int id) {
      try (Session session = DB.getSessionFactory().openSession()) {
         return session.find(MemberModel.class, id);
      } catch (Exception e) {
         System.err.println("Failed to retrieve member: " + e.getMessage());
         return null;
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
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Failed to update member: " + e.getMessage());
         return false;
      }
   }

   public boolean updateStatus(int id, String status) {
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
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Failed to set member status: " + e.getMessage());
         return false;
      }
   }

   public List<MemberModel> getAllMembers() {
      try (Session session = DB.getSessionFactory().openSession()) {
         return new JPAQueryFactory(session)
               .selectFrom(qMember)
               .orderBy(qMember.id.asc())
               .fetch();

      } catch (Exception e) {
         System.err.println("Failed to retrieve members: " + e.getMessage());
         return List.of();
      }
   }

}