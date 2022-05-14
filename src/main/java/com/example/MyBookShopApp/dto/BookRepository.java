package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;


public interface BookRepository extends JpaRepository<Book, Integer> {

    Page<Book> findByOrderByPubDateDesc(Pageable pageable);

    Page<Book> findAllByPubDateBetweenOrderByPubDateDesc(Date fromDate, Date toDate, Pageable nextPage);

    Page<Book> findByPubDateLessThanEqualOrderByPubDateDesc(Date toDate, Pageable nextPage);

    Page<Book> findByPubDateGreaterThanEqualOrderByPubDateDesc(Date fromDate, Pageable nextPage);

    @Query(value = "select new com.example.MyBookShopApp.entity.Book(b.id, b.pubDate, b.isBestseller, b.slug, b.title, b.image, b.description, b.price, b.discount) " +
            "from Book b inner join AuthorBooks a2b on b.id = a2b.book.id inner join Author a on a2b.author.id = a.id where a.slug = :authorSlug")
    Page<Book> findBooksByAuthorNameContaining(@Param("authorSlug") String authorName, Pageable nextPage);

    @Query(value = "select b from Book b inner join b.genresList g where g.slug = :genreSlug")
    Page<Book> findBooksByGenreSlug(String genreSlug, Pageable nextPage);

    @Query(value = "select b from Book b inner join b.tagsList t where t.slug = :tagSlug")
    Page<Book> findBooksByTagSlug(String tagSlug, Pageable nextPage);

    @Query(value = "SELECT b.* FROM book b LEFT JOIN " +
            "(SELECT b2u.book_id, " +
                    "SUM(CASE WHEN b2ut.code = 'PAID' THEN 1 ELSE 0 END) AS bought, " +
                    "SUM(CASE WHEN b2ut.code = 'CART' THEN 1 ELSE 0 END) AS cart,   " +
                    "SUM(CASE WHEN b2ut.code = 'KEPT' THEN 1 ELSE 0 END) AS kept    " +
            " FROM book2user b2u JOIN book2user_type b2ut ON b2u.type_id = b2ut.id  " +
            " GROUP BY b2u.book_id" +
            ") d1 ON b.id = d1.book_id " +
            "LEFT JOIN (SELECT b2uv.book_id, COUNT(b2uv.book_id) AS viewed FROM book2user_view b2uv GROUP BY b2uv.book_id) d2 ON b.id = d2.book_id" +
            " ORDER BY COALESCE(d1.bought, 0) + 0.7 * COALESCE(d1.cart, 0) + 0.4 * COALESCE(d1.kept, 0) + 0.1 * COALESCE(d2.viewed, 0) DESC", nativeQuery = true)
    Page<Book> findBooksByPopular(Pageable nextPage);

    @Query(value = "SELECT b.* FROM book b " +
            "LEFT JOIN (SELECT br.book_id, ROUND(SUM(br.value) * 1.0 / COUNT(br.book_id)) AS rate FROM book_rate br GROUP BY br.book_id) r ON b.id = r.book_id " +
            "ORDER BY COALESCE(r.rate, 0) DESC, b.pub_date DESC", nativeQuery = true)
    Page<Book> findRecommendBooksByRate(Pageable nextPage);

    @Query(value = "SELECT b.* FROM book b JOIN " +
            "(SELECT b2a_ab.book_id FROM \"userRecommendBooks\" urb " +
            " JOIN book2author b2a ON b2a.book_id = urb.book_id " +
            " JOIN book2author b2a_ab ON b2a_ab.author_id = b2a.author_id   " +
            " WHERE urb.user_id = :userId AND urb.book_id != b2a_ab.book_id " +
            "UNION " +
            " SELECT b2g_ab.book_id FROM \"userRecommendBooks\" urb " +
            " JOIN book2genre b2g ON b2g.book_id = urb.book_id  " +
            " JOIN book2genre b2g_ab ON b2g_ab.genre_id = b2g.genre_id      " +
            " WHERE urb.user_id = :userId AND urb.book_id != b2g_ab.book_id " +
            "UNION " +
            " SELECT b2t_ab.book_id FROM \"userRecommendBooks\" urb " +
            " JOIN book2tag b2t ON b2t.book_id = urb.book_id    " +
            " JOIN book2tag b2t_ab ON b2t_ab.tag_id = b2t.tag_id" +
            " WHERE urb.user_id = :userId AND urb.book_id != b2t_ab.book_id " +
            ") ub ON b.id = ub.book_id ORDER BY b.pub_date DESC", nativeQuery = true)
    Page<Book> findRecommendBooksByUser(Integer userId, Pageable nextPage);

    // New Book REST repository commands
    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceBetween(Integer min, Integer max);

    List<Book> findBooksByPriceIs(Integer price);

    @Query("from Book where isBestseller = 1")
    List<Book> getBestsellers();

    @Query(value = "SELECT * FROM book WHERE discount = (SELECT MAX(discount) FROM book)", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

    Page<Book> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    Book findBookBySlugEquals(String slug);

    Book findBookById(Integer bookId);

    List<Book> findBooksBySlugIn(String[] slugs);

}