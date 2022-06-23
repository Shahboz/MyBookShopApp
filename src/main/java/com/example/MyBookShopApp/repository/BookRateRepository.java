package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.BookRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface BookRateRepository extends JpaRepository<BookRate, Integer> {

    List<BookRate> findBookRatesByBookSlugAndValueEquals(String bookSlug, Integer rateValue);

    Integer countByBookSlug(String bookSlug);

    @Query(value = "select coalesce(round(sum(value) * 1.0 / count(book.id)), 0) from BookRate where book.slug = :bookSlug")
    Integer calcBookRate(String bookSlug);

    BookRate findBookRateByBookIdAndUserId(Integer bookId, Integer userId);

}