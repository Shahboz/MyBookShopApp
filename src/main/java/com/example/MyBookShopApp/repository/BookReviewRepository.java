package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.BookReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    @Query(value = "select br from BookReview br left join BookReviewLike brl on br.id = brl.review.id where br.book.slug = :bookSlug " +
            "group by br order by sum(case when brl.value = 1 then 1 else 0 end) - sum(case when brl.value = -1 then 1 else 0 end) desc")
    List<BookReview> findBookReviewsByBookSlug(String bookSlug, Pageable nextPage);

    @Query(value = "select br from BookReview br left join BookReviewLike  brl on br.id = brl.review.id where br.user.hash = :userHash " +
            "group by br order by sum(case when brl.value = 1 then 1 else 0 end) - sum(case when brl.value = -1 then 1 else 0 end) desc")
    List<BookReview> findBookReviewsByUserHash(String userHash);

    BookReview findBookReviewByBookIdAndUserId(Integer bookId, Integer userId);

    BookReview findBookReviewById(Integer reviewId);

    Integer countByBookSlug(String bookSlug);

}