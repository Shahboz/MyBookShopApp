package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GenreRepository extends JpaRepository<Genre, Integer> {

    Genre findGenreBySlug(String slug);

    @Query(value = "with recursive genres as (" +
            "select g.id, g.parent_id, 1 as depth from genre g where g.id = :genre_id " +
            "union select g.id, g.parent_id, p.depth + 1 as depth from genres p " +
            "inner join genre g on g.parent_id = p.id) " +
            "select max(depth) from genres", nativeQuery = true)
    Integer findDeepByGenre(@Param("genre_id") Integer genreId);

    @Query(value = "select b from Book b inner join b.genresList g where g.slug = :genreSlug")
    Page<Book> findBooksByGenreSlug(@Param("genreSlug") String genreSlug, Pageable nextPage);

}
