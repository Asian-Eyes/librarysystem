package org.example.Repository;

import org.example.Model.QUserModel;
import org.example.Model.UserModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class AuthRepo {

   private static final QUserModel qUser = QUserModel.userModel;

   public boolean createUser(UserModel user) {
      Transaction tx = null;

      try (Session session = DB.getSessionFactory().openSession()) {
         tx = session.beginTransaction();
         session.persist(user);
         tx.commit();
         return true;
      } catch (Exception e) {
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Create user failed: " + e.getMessage());
         return false;
      }
   }

   public UserModel findByUsernameOrEmail(String input) {
      try (Session session = DB.getSessionFactory().openSession()) {
         return new JPAQueryFactory(session)
               .selectFrom(qUser)
               .where(qUser.username.eq(input).or(qUser.email.eq(input)))
               .fetchOne();
      } catch (Exception e) {
         System.err.println("Find user failed: " + e.getMessage());
         return null;
      }
   }

   public boolean existsByUsername(String username) {
      try (Session session = DB.getSessionFactory().openSession()) {
         Long count = new JPAQueryFactory(session)
               .select(qUser.count())
               .from(qUser)
               .where(qUser.username.eq(username))
               .fetchOne();
         return count != null && count > 0;
      } catch (Exception e) {
         System.err.println("Username check failed: " + e.getMessage());
         return false;
      }
   }

   public boolean existsByEmail(String email) {
      try (Session session = DB.getSessionFactory().openSession()) {
         Long count = new JPAQueryFactory(session)
               .select(qUser.count())
               .from(qUser)
               .where(qUser.email.eq(email))
               .fetchOne();
         return count != null && count > 0;
      } catch (Exception e) {
         System.err.println("Email check failed: " + e.getMessage());
         return false;
      }
   }
}