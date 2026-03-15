package org.example.Repository;

import org.example.Model.UserModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;

public class AuthRepo {

    public boolean signUp(String username, String email, String passwordHash, String role) {
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            Query<Long> check = session.createQuery(
                    "SELECT COUNT(u) FROM UserModel u WHERE u.username = :username OR u.email = :email",
                    Long.class);
            check.setParameter("username", username);
            check.setParameter("email", email);
            if (check.uniqueResult() > 0) {
                System.err.println("Username or email already exists.");
                return false;
            }
            UserModel user = new UserModel();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(passwordHash);
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Sign up failed: " + e.getMessage());
            return false;
        }
    }

    public UserModel signIn(String usernameOrEmail, String passwordHash) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Query<UserModel> query = session.createQuery(
                    "FROM UserModel u WHERE (u.username = :input OR u.email = :input) AND u.passwordHash = :passwordHash",
                    UserModel.class);
            query.setParameter("input", usernameOrEmail);
            query.setParameter("passwordHash", passwordHash);
            UserModel user = query.uniqueResult();
            if (user == null) {
                System.err.println("Invalid credentials.");
            }
            return user;
        } catch (Exception e) {
            System.err.println("Sign in failed: " + e.getMessage());
            return null;
        }
    }
}