package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag findTagBySlug(String tagSlug);

    @Query(value = "select b from Book b inner join b.tagsList t where t.slug = :slug")
    Page<Book> findBooksByTag(@Param("slug") String slugTag, Pageable nextPage);

}
