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

    @Query(value = "SELECT new com.example.MyBookShopApp.entity.Book(b.id, b.pubDate, b.isBestseller, b.slug, b.title, b.image, b.description, b.price, b.discount) " +
            "FROM Book b inner join AuthorBooks a2b on b.id = a2b.book.id inner join Author a on a2b.author.id = a.id where a.slug = :authorSlug")
    Page<Book> findBooksByAuthorNameContaining(@Param("authorSlug") String authorName, Pageable nextPage);

    @Query(value = "select b from Book b inner join b.genresList g where g.slug = :genreSlug")
    Page<Book> findBooksByGenreSlug(String genreSlug, Pageable nextPage);

    @Query(value = "select b from Book b inner join b.tagsList t where t.slug = :tagSlug")
    Page<Book> findBooksByTagSlug(String tagSlug, Pageable nextPage);

    @Query(value = "from Book b order by b.boughtUsers + 0.7 * b.cardUsers + 0.4 * b.holdUsers desc")
    Page<Book> findBooksByPopular(Pageable nextPage);

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