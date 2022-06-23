package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.UserViewedBooksRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserViewedBooks;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
public class UserViewedBooksService {

    private final BookstoreUserRegister userRegister;
    private final UserViewedBooksRepository userViewedBookRepository;

    @Autowired
    public UserViewedBooksService(BookstoreUserRegister userRegister, UserViewedBooksRepository userViewedBookRepository) {
        this.userRegister = userRegister;
        this.userViewedBookRepository = userViewedBookRepository;
    }

    public void saveBookView(Book book) {
        User currentUser = (User) userRegister.getCurrentUser();
        if (book != null && currentUser != null) {
            UserViewedBooks userViewedBook = userViewedBookRepository.findUserViewedBooksByUserIdAndBookId(currentUser.getId(), book.getId());
            if (userViewedBook == null) {
                userViewedBook = new UserViewedBooks();
                userViewedBook.setUser(currentUser);
                userViewedBook.setBook(book);
                userViewedBook.setTime(new Date());
            } else {
                userViewedBook.setTime(new Date());
            }
            userViewedBookRepository.save(userViewedBook);
        }
    }

    public Page<Book> getPageOfViewedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        User currentUser = (User) userRegister.getCurrentUser();
        return userViewedBookRepository.findUserViewedBooksByUserId(currentUser == null ? 0 : currentUser.getId(), nextPage);
    }

}