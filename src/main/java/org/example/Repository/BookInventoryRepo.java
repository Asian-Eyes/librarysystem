package org.example.Repository;

import java.util.List;

import org.example.Model.BooksModel;
import org.example.Model.QBooksModel;
import org.example.util.DB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class BookInventoryRepo {

   private static final QBooksModel qBook = QBooksModel.booksModel;

   public List<BooksModel> getAllBooks() {
      try (Session session = DB.getSessionFactory().openSession()) {
         return new JPAQueryFactory(session)
               .selectFrom(qBook)
               .orderBy(qBook.id.asc())
               .fetch();
      } catch (Exception e) {
         System.err.println("Failed to retrieve books: " + e.getMessage());
         return List.of();
      }
   }

   public BooksModel getBookById(int id) {
      try (Session session = DB.getSessionFactory().openSession()) {
         return session.find(BooksModel.class, id);
      } catch (Exception e) {
         System.err.println("Failed to retrieve book: " + e.getMessage());
         return null;
      }
   }

   public boolean addBook(BooksModel book) {
      Transaction tx = null;
      try (Session session = DB.getSessionFactory().openSession()) {
         tx = session.beginTransaction();
         session.persist(book);
         tx.commit();
         return true;
      } catch (Exception e) {
         if (tx != null) {
            tx.rollback();
         }
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
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Failed to update book: " + e.getMessage());
         return false;
      }
   }

   public boolean deleteBook(int id) {
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
         if (tx != null) {
            tx.rollback();
         }
         System.err.println("Failed to delete book: " + e.getMessage());
         return false;
      }
   }

   public BooksModel findByIsbn(String isbn) {
      try (Session session = DB.getSessionFactory().openSession()) {
         return new JPAQueryFactory(session)
               .selectFrom(qBook)
               .where(qBook.isbn.eq(isbn))
               .fetchOne();
      } catch (Exception e) {
         System.err.println("ISBN lookup failed: " + e.getMessage());
         return null;
      }
   }
}