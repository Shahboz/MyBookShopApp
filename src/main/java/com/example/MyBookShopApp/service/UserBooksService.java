package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.repository.UserBookTypeRepository;
import com.example.MyBookShopApp.repository.UserBooksRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserBookType;
import com.example.MyBookShopApp.entity.UserBooks;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class UserBooksService {

    private final UserBooksRepository userBooksRepository;
    private final UserBookTypeRepository bookTypeRepository;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public UserBooksService(UserBooksRepository userBooksRepository, UserBookTypeRepository bookTypeRepository, BookstoreUserRegister userRegister) {
        this.userBooksRepository = userBooksRepository;
        this.bookTypeRepository = bookTypeRepository;
        this.userRegister = userRegister;
    }

    public List<Book> getUserPaidBooks() {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser == null ? new ArrayList<>() : userBooksRepository.findUserBooksByUserBookType(currentUser.getId(), "PAID");
    }

    public List<Book> getUserCartBooks() {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser == null ? new ArrayList<>() : userBooksRepository.findUserBooksByUserBookType(currentUser.getId(), "CART");
    }

    public List<Book> getUserKeptBooks() {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser == null ? new ArrayList<>() : userBooksRepository.findUserBooksByUserBookType(currentUser.getId(), "KEPT");
    }

    public List<Book> getArchivedBooks() {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser == null ? new ArrayList<>() : userBooksRepository.findUserBooksByUserBookType(currentUser.getId(), "ARCHIVED");
    }

    public ResultResponse changeBookStatus(String status, Book book) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();

        if (StringUtils.isEmpty(status)) {
            result.setResult(false);
            result.setError("Не указан статус");
        } else if (book == null) {
          result.setResult(false);
          result.setError("Книга не найдена");
        } else if (currentUser != null) {
            // Удаление из корзины/отложенных
            if (status.equalsIgnoreCase("UNLINK")) {
                UserBooks userCartBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "CART");
                UserBooks userKeptBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "KEPT");
                if (userCartBook != null) {
                    userBooksRepository.delete(userCartBook);
                } else if (userKeptBook != null) {
                    userBooksRepository.delete(userKeptBook);
                }
                result.setResult(true);
            } else if (status.equalsIgnoreCase("CART") || status.equalsIgnoreCase("KEPT")) {
                UserBooks userPaidBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "PAID");
                UserBooks userArchivedBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "ARCHIVED");
                if (userPaidBook == null && userArchivedBook == null) {
                    UserBookType userBookType = bookTypeRepository.findUserBookTypeByCode(status);
                    UserBooks userBook;
                    UserBooks userCartBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "CART");
                    UserBooks userKeptBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "KEPT");
                    // Книга в корзине
                    if (userCartBook != null) {
                        userBook = userCartBook;
                    } else if (userKeptBook != null) {
                        userBook = userKeptBook;
                    } else {
                        userBook = new UserBooks();
                        userBook.setUser(currentUser);
                        userBook.setBook(book);
                    }
                    userBook.setType(userBookType);
                    userBook.setTime(new Date());
                    userBooksRepository.save(userBook);
                    result.setResult(true);
                } else if (status.equalsIgnoreCase("KEPT")) {
                    result.setResult(false);
                    result.setError("Нельзя отложить купленную книгу");
                } else {
                    result.setResult(false);
                    result.setError("Книга уже куплена");
                }
            } else if (status.equalsIgnoreCase("ARCHIVED")) {
                UserBooks userPaidBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "PAID");
                UserBooks userArchivedBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "ARCHIVED");
                if (userPaidBook != null) {
                    UserBookType archiveType = bookTypeRepository.findUserBookTypeByCode("ARCHIVED");
                    userPaidBook.setType(archiveType);
                    userPaidBook.setTime(new Date());
                    userBooksRepository.save(userPaidBook);
                    result.setResult(true);
                } else if (userArchivedBook != null) {
                    UserBookType paidType = bookTypeRepository.findUserBookTypeByCode("PAID");
                    userArchivedBook.setType(paidType);
                    userArchivedBook.setTime(new Date());
                    userBooksRepository.save(userArchivedBook);
                    result.setResult(true);
                } else {
                    result.setResult(false);
                    result.setError("Книга не куплена/не архивирована");
                }
            } else if (status.equalsIgnoreCase("PAID")) {
                UserBooks userCartBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "CART");
                UserBooks userArchivedBook = userBooksRepository.findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(currentUser.getId(), book.getSlug(), "ARCHIVED");
                if (userCartBook == null && userArchivedBook == null) {
                    result.setResult(false);
                    result.setError("Книга не найдена в корзине");
                } else {
                    UserBooks userBooks = userCartBook != null ? userCartBook : userArchivedBook;
                    UserBookType paidType = bookTypeRepository.findUserBookTypeByCode("PAID");
                    userBooks.setType(paidType);
                    userBooks.setTime(new Date());
                    userBooksRepository.save(userBooks);
                    result.setResult(true);
                }
            } else {
                result.setResult(false);
                result.setError("Некорректный статус");
            }
        } else {
            result.setResult(true);
        }
        return result;
    }

}