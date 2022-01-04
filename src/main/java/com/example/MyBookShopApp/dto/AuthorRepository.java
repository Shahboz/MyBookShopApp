package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("from Author where slug = :slug")
    Author findBySlug(@Param("slug") String slug);

    @Query(value = "SELECT new com.example.MyBookShopApp.entity.Book(b.id, b.pubDate, b.isBestseller, b.slug, b.title, b.image, b.description, b.price, b.discount) " +
                   "FROM Book b inner join AuthorBooks a2b on b.id = a2b.book.id inner join Author a on a2b.author.id = a.id where a.slug = :authorSlug")
    Page<Book> findBooksBySlug(@Param("authorSlug") String authorSlug, Pageable nextPage);
}
