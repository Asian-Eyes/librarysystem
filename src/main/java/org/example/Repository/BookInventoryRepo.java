package org.example.Repository;

import org.example.Model.BooksModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class BookInventoryRepo {

    public boolean addBook(BooksModel book) {
        if (findByIsbn(book.getIsbn()) != null) {
            System.err.println("A book with ISBN " + book.getIsbn() + " already exists.");
            return false;
        }
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(book);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to add book: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(int id, String title, String author, double price, int stock) {
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            BooksModel book = session.get(BooksModel.class, id);
            if (book == null) {
                System.err.println("No book found with ID " + id);
                return false;
            }
            tx = session.beginTransaction();
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
            book.setStock(stock);
            session.merge(book);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to update book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int id) {
        if (hasActiveBorrows(id)) {
            System.err.println("Cannot delete book ID " + id + ": it has active borrow slips.");
            return false;
        }
        Transaction tx = null;
        try (Session session = DB.getSessionFactory().openSession()) {
            BooksModel book = session.get(BooksModel.class, id);
            if (book == null) {
                System.err.println("No book found with ID " + id);
                return false;
            }
            tx = session.beginTransaction();
            session.remove(book);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Failed to delete book: " + e.getMessage());
            return false;
        }
    }

    private boolean hasActiveBorrows(int bookId) {
        try (Session session = DB.getSessionFactory().openSession()) {

            String hql = """
            SELECT COUNT(bi)
            FROM BorrowItemsModel bi
            WHERE bi.book.id = :bookId
            AND bi.borrowSlip.returned = false
        """;

            Long count = session.createQuery(hql, Long.class)
                    .setParameter("bookId", bookId)
                    .uniqueResult();

            return count != null && count > 0;

        } catch (Exception e) {
            System.err.println("Could not verify active borrows: " + e.getMessage());
            return true;
        }
    }

    public BooksModel findByIsbn(String isbn) {
        try (Session session = DB.getSessionFactory().openSession()) {
            Query<BooksModel> query = session.createQuery(
                    "FROM BooksModel b WHERE b.isbn = :isbn", BooksModel.class);
            query.setParameter("isbn", isbn);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("ISBN lookup failed: " + e.getMessage());
            return null;
        }
    }
}