package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.repository.BookFileRepository;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.FileDownloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookServiceTests {

    private Integer bookId;
    private String bookSlug;
    private String simpleSlug;
    private Date fromDateFilter;
    private Date toDateFilter;
    private Book book;
    private BookInfoDto bookInfoDto;
    private String[] booksSlug;
    private FileDownload fileDownload;
    private List<Book> expectedBookList = new ArrayList<>();
    private final BookService bookService;

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private FileDownloadRepository fileDownloadRepository;
    @MockBean
    private BookFileRepository bookFileRepository;

    @Autowired
    BookServiceTests(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    void setup() {
        bookId = 1;
        bookSlug = "slug";
        simpleSlug = "simple";

        fromDateFilter = new Date(0L);
        toDateFilter = new Date();

        book = new Book();
        book.setId(bookId);
        book.setSlug(bookSlug);
        book.setTitle("Books title");
        book.setPubDate(new Date());
        book.setDescription("Description of book");
        book.setImage("Default image");
        book.setPrice(500);
        book.setDiscount(30);
        book.setIsBestseller(0);

        bookInfoDto = new BookInfoDto(book);

        booksSlug = new String[]{"slug1", "slug2"};

        fileDownload = new FileDownload();
        fileDownload.setId(2);
        fileDownload.setBook(book);
        fileDownload.setUser(new User());
        fileDownload.setCount(0);

        for (int i = 4; i >= 0; i--) {
            Book book = new Book();
            book.setSlug("Slug" + i);
            expectedBookList.add(book);
        }
    }

    @AfterEach
    void tearDown() {
        bookId = 0;
        bookSlug = null;
        simpleSlug = null;
        fromDateFilter = null;
        toDateFilter = null;
        book = null;
        bookInfoDto = null;
        booksSlug = null;
        fileDownload = null;
        expectedBookList = null;
    }

    private void checkExpectedBookList(List<Book> bookList) {
        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());
        assertThat(bookList).hasSize(5);
        assertTrue(bookList.get(0).getSlug().contains("4"));
        assertTrue(bookList.get(4).getSlug().contains("0"));
    }

    @Test
    void testGetPageOfNewBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findAllByPubDateBetweenOrderByPubDateDesc(fromDateFilter, toDateFilter, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfNewBooks(0, 5, fromDateFilter, toDateFilter).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfNewBooksWithoutDates() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findByOrderByPubDateDesc(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfNewBooks(0, 5, null, null).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfNewBooksFromDate() {

        Mockito.doReturn(new PageImpl<>(expectedBookList.subList(2, 5)))
                .when(bookRepository)
                .findByPubDateGreaterThanEqualOrderByPubDateDesc(fromDateFilter, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfNewBooks(0, 5, fromDateFilter, null).getContent();

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());
        assertThat(bookList).hasSize(3);
    }

    @Test
    void testGetPageOfNewBooksToDate() {

        Mockito.doReturn(new PageImpl<>(expectedBookList.subList(0, 2)))
                .when(bookRepository)
                .findByPubDateLessThanEqualOrderByPubDateDesc(toDateFilter, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfNewBooks(0, 5, null, toDateFilter).getContent();

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());
        assertThat(bookList).hasSize(2);
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testGetPageOfRecommendedBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findRecommendBooksByUser(Mockito.any(Integer.class), Mockito.any(Pageable.class));
        Mockito.doReturn(2)
                .when(bookRepository)
                .getCountUserRecommendedBooks(Mockito.any(Integer.class));

        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfRecommendedBooksAnonymousUser() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findRecommendBooksByRate(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfSearchResultBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findAll(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfSearchResultBooks(null, 0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfPopularBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findBooksByPopular(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfPopularBooks(0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfAuthorBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findBooksByAuthorNameContaining(simpleSlug, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfAuthorBooks(simpleSlug, 0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfAuthorBooksFail() {
        List<Book> bookList = bookService.getPageOfAuthorBooks(null, 0, 5).getContent();

        assertThat(bookList).isEmpty();
    }

    @Test
    void testGetPageOfGenreBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findBooksByGenreSlug(simpleSlug, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfGenreBooks(simpleSlug, 0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfGenreBooksFail() {
        List<Book> bookList = bookService.getPageOfGenreBooks(null, 0, 5).getContent();

        assertThat(bookList).isEmpty();
    }

    @Test
    void testGetPageOfTagBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findBooksByTagSlug(simpleSlug, PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfTagBooks(simpleSlug, 0, 5).getContent();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetPageOfTagBooksFail() {
        List<Book> bookList = bookService.getPageOfTagBooks(null, 0, 5).getContent();

        assertThat(bookList).isEmpty();
    }

    @Test
    void testGetBooksWithPriceBetween() {

        Mockito.doReturn(expectedBookList)
                .when(bookRepository)
                .findBooksByPriceBetween(0, 100);

        List<Book> bookList = bookService.getBooksWithPriceBetween(0, 100);

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetBestsellers() {

        Mockito.doReturn(expectedBookList)
                .when(bookRepository)
                .getBestsellers();

        List<Book> bookList = bookService.getBestsellers();

        checkExpectedBookList(bookList);
    }

    @Test
    void testGetBooksWithMaxDiscount() {

        Mockito.doReturn(expectedBookList)
                .when(bookRepository)
                .getBooksWithMaxDiscount();

        List<Book> bookList = bookService.getBooksWithMaxDiscount();

        checkExpectedBookList(bookList);
    }

    @Test
    void testEmptyBookBySlug() {
        Book b = bookService.getBookBySlug("");
        assertNull(b);
    }

    @Test
    void testGetBookById() {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookById(bookId);

        Book book = bookService.getBookById(bookId);

        assertNotNull(book);
        assertEquals(book.getId(), bookId);
    }

    @Test
    void testGetBooksBySlugs() {

        Mockito.doReturn(expectedBookList)
                .when(bookRepository)
                .findBooksBySlugIn(booksSlug);

        List<Book> bookList = bookService.getBooksBySlugs(booksSlug);

        checkExpectedBookList(bookList);
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testLimitDownloadExceeded() {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);
        Mockito.doReturn(fileDownload)
                .when(fileDownloadRepository)
                .findFileDownloadByUserIdAndBookSlug(Mockito.any(Integer.class), Mockito.any(String.class));

        fileDownload.setCount(20);
        Boolean isExceeded = bookService.limitDownloadExceded(bookSlug);

        assertTrue(isExceeded);
    }

    @Test
    void testNotLimitDownloadExceeded() {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);
        Mockito.doReturn(fileDownload)
                .when(fileDownloadRepository)
                .findFileDownloadByUserIdAndBookSlug(Mockito.any(Integer.class), Mockito.any(String.class));

        Boolean isExceeded = bookService.limitDownloadExceded(bookSlug);

        assertFalse(isExceeded);
    }

    @Test
    void testGetBookFileData() throws IOException {
        List<BookFile> bookFileList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BookFile bookFile = new BookFile();
            bookFile.setId(i);
            bookFile.setBook(book);
            bookFile.setHash(simpleSlug);
            bookFile.setPath("/Locke.pdf");

            bookFileList.add(bookFile);
        }

        Mockito.doReturn(bookFileList)
                .when(bookFileRepository)
                .findAllByBookSlugOrderByTypeNameAsc(bookSlug);
        Mockito.doReturn(bookFileList.get(0))
                .when(bookFileRepository)
                .findFileByHash(simpleSlug);

        List<BookFileDto> bookFilesDto = bookService.getBookFilesData(bookSlug);

        assertNotNull(bookFilesDto);
        assertThat(bookFilesDto).hasSize(3);
        assertEquals(bookFileList.get(0).getBook(), this.book);
        assertEquals(bookFileList.get(1).getBook(), this.book);
        assertEquals(bookFileList.get(2).getBook(), this.book);
    }

    @Test
    void testGetEmptyBookFileDataFail() throws IOException {

        List<BookFileDto> bookFilesDto = bookService.getBookFilesData("");

        assertNotNull(bookFilesDto);
        assertThat(bookFilesDto).isEmpty();
    }

    @Test
    void testGetBookInfoDto() throws IOException {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);

        BookInfoDto dto = bookService.getBookInfoDto(bookSlug);

        assertNotNull(dto);
        assertEquals(dto.getId(), bookInfoDto.getId());
        assertEquals(dto.getSlug(), bookInfoDto.getSlug());
        assertEquals(dto.getTitle(), bookInfoDto.getTitle());
        assertThat(dto.getGenreList()).isEmpty();
        assertThat(dto.getTagList()).isEmpty();
        assertThat(dto.getBookFileDtoList()).isEmpty();
    }

    @Test
    void testGetEmptyBookInfoDtoFail() throws IOException {

        Mockito.doReturn(null)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);

        BookInfoDto dto = bookService.getBookInfoDto(bookSlug);

        assertNull(dto);
    }

    @Test
    void testSaveBook() {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);

        bookInfoDto.setTitle(bookInfoDto.getTitle() + "_update");
        bookInfoDto.setDescription(bookInfoDto.getDescription() + " is updated.");
        bookInfoDto.setPubDate("2000-01-01T00:00");
        bookInfoDto.setPrice(bookInfoDto.getPrice() * 2);
        bookInfoDto.setDiscount(bookInfoDto.getDiscount() + 5);
        bookInfoDto.setIsBestseller(true);

        bookService.saveBook(bookInfoDto);

        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any(Book.class));
    }

    @Test
    void testDeleteBook() {

        Mockito.doReturn(book)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);

        bookService.deleteBook(bookSlug);

        Mockito.verify(bookRepository, Mockito.times(1)).delete(Mockito.any(Book.class));
    }

    @Test
    void testDeleteEmptyBookFail() {

        Mockito.doReturn(null)
                .when(bookRepository)
                .findBookBySlugEquals(bookSlug);

        bookService.deleteBook(bookSlug);

        Mockito.verify(bookRepository, Mockito.times(0)).delete(Mockito.any(Book.class));
    }

}