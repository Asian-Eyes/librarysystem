package org.example.Repository;

import org.example.Model.UserModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class AuthRepo {

    public boolean signUp(String username, String email, String password, String role) {
        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            UserModel user = new UserModel();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());

            session.persist(user);

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();

            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                System.err.println("Username or email already exists.");
            } else {
                System.err.println("Sign up failed: " + e.getMessage());
            }

            return false;
        }
    }

    public UserModel signIn(String usernameOrEmail, String password) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Query<UserModel> query = session.createQuery(
                    "FROM UserModel u WHERE u.username = :input OR u.email = :input",
                    UserModel.class);
            query.setParameter("input", usernameOrEmail);
            UserModel user = query.uniqueResult();
            if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
                System.err.println("Invalid credentials.");
                return null;
            }
            return user;
        } catch (Exception e) {
            System.err.println("Sign in failed: " + e.getMessage());
            return null;
        }
    }
}