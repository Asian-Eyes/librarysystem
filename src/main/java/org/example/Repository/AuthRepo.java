package org.example.Repository;

import org.example.Model.UserModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class AuthRepo {

    public boolean signUp(String username, String email, String password, String role) {
        Transaction tx = null;

        try (Session session = DB.getSessionFactory().openSession()) {

            if (existsByUsername(session, username)) {
                System.err.println("Username already exists.");
                return false;
            }

            if (existsByEmail(session, email)) {
                System.err.println("Email already exists.");
                return false;
            }

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
            System.err.println("Sign up failed: " + e.getMessage());
            return false;
        }
    }

    public UserModel signIn(String usernameOrEmail, String password) {
        try (Session session = DB.getSessionFactory().openSession()) {

            UserModel user = findByUsernameOrEmail(session, usernameOrEmail);

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

    private UserModel findByUsernameOrEmail(Session session, String input) {

        String hql = "FROM UserModel u WHERE u.username = :input";
        UserModel user = session.createQuery(hql, UserModel.class)
                .setParameter("input", input)
                .uniqueResult();

        if (user != null) return user;

        hql = "FROM UserModel u WHERE u.email = :input";
        return session.createQuery(hql, UserModel.class)
                .setParameter("input", input)
                .uniqueResult();
    }

    private boolean existsByUsername(Session session, String username) {
        String hql = "SELECT COUNT(u) FROM UserModel u WHERE u.username = :username";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("username", username)
                .uniqueResult();
        return count != null && count > 0;
    }

    private boolean existsByEmail(Session session, String email) {
        String hql = "SELECT COUNT(u) FROM UserModel u WHERE u.email = :email";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("email", email)
                .uniqueResult();
        return count != null && count > 0;
    }
}