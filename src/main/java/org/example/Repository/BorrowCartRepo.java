package org.example.Repository;

import java.util.List;

import org.example.Model.BooksModel;
import org.example.Model.QBooksModel;
import org.example.util.DB;
import org.hibernate.Session;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class BorrowCartRepo {

   private static final QBooksModel qBook = QBooksModel.booksModel;

   public List<BooksModel> searchBooks(String keyword) {
      try (Session session = DB.getSessionFactory().openSession()) {
         JPAQueryFactory queryFactory = new JPAQueryFactory(session);
         String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

         if (normalizedKeyword.isBlank()) {
            return queryFactory.selectFrom(qBook)
                  .orderBy(qBook.id.asc())
                  .fetch();
         }

         return queryFactory.selectFrom(qBook)
               .where(
                     qBook.isbn.lower().contains(normalizedKeyword)
                           .or(qBook.title.lower().contains(normalizedKeyword))
                           .or(qBook.author.lower().contains(normalizedKeyword)))
               .orderBy(qBook.title.asc())
               .fetch();
      } catch (Exception e) {
         System.err.println("Search failed: " + e.getMessage());
         return List.of();
      }
   }
}