package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.FileDownloadRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.FileDownload;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileDownloadService {

    @Value("${book.download.limit}")
    private Integer maxDownloadLimit;

    private final BookstoreUserRegister userRegister;
    private final FileDownloadRepository fileDownloadRepository;

    @Autowired
    public FileDownloadService(BookstoreUserRegister userRegister, FileDownloadRepository fileDownloadRepository) {
        this.userRegister = userRegister;
        this.fileDownloadRepository = fileDownloadRepository;
    }

    public boolean isLimitDownloadExceeded(Book book) {
        boolean result = false;
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser != null && book != null) {
            FileDownload fileDownload = fileDownloadRepository.findFileDownloadByUserIdAndBookSlug(currentUser.getId(), book.getSlug());
            if (fileDownload != null && fileDownload.getCount() > maxDownloadLimit) {
                result = true;
            }
        }
        return result;
    }

    public boolean saveDownloadBook(Book book) {
        boolean result = false;
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser != null && book != null) {
            FileDownload fileDownload = fileDownloadRepository.findFileDownloadByUserIdAndBookSlug(currentUser.getId(), book.getSlug());
            if (fileDownload == null) {
                fileDownload = new FileDownload();
                fileDownload.setBook(book);
                fileDownload.setUser(currentUser);
                fileDownload.setCount(1);
            } else {
                fileDownload.setCount(fileDownload.getCount() + 1);
            }
            fileDownloadRepository.save(fileDownload);
            result = true;
        }
        return result;
    }

}