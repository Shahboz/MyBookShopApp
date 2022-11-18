package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.AuthorBooks;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.repository.AuthorBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AuthorBooksService {

    private final AuthorBooksRepository authorBooksRepository;

    @Autowired
    public AuthorBooksService(AuthorBooksRepository authorBooksRepository) {
        this.authorBooksRepository = authorBooksRepository;
    }

    public List<AuthorBooks> getAuthorsBook(String bookSlug) {
        return authorBooksRepository.findAuthorsBooksByBookSlug(bookSlug);
    }

    public void saveAuthorBook(Book book, Author author) {
        if (author != null && book != null && !book.getAuthors().contains(author)) {
            List<AuthorBooks> authorBooksList = authorBooksRepository.findAuthorsBooksByBookSlug(book.getSlug());
            int maxSortIndex = authorBooksList.stream().mapToInt(a -> a.getSortIndex()).reduce(Math::max).orElse(0);
            AuthorBooks authorBooks = new AuthorBooks(book, author, maxSortIndex + 1);
            authorBooksRepository.save(authorBooks);
        }
    }

    public void deleteAuthorBook(Integer authorBookId) {
        AuthorBooks authorBook = authorBooksRepository.findAuthorBooksById(authorBookId);
        if (authorBook != null) {
            authorBooksRepository.delete(authorBook);
        }
    }

}