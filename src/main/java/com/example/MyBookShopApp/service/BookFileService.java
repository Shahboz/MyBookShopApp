package com.example.MyBookShopApp.service;


import com.example.MyBookShopApp.aspect.annotations.BookFileInfo;
import com.example.MyBookShopApp.dto.BookFileRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookFile;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
public class BookFileService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${download.path}")
    private String downloadPath;

    private final BookFileRepository bookFileRepository;

    @Autowired
    public BookFileService(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    public List<BookFile> getBookFilesByBookSlug(String bookSlug) {
        return bookFileRepository.findAllByBookSlugOrderByTypeNameAsc(bookSlug);
    }

    public Book getBookByFileHash(String fileHash) {
        return bookFileRepository.findBookByHash(fileHash);
    }

    @BookFileInfo
    public String saveBookImage(MultipartFile file, String bookSlug) throws IOException {
        String resourceURI = null;
        if(!file.isEmpty()) {
            if(!new java.io.File(uploadPath).exists()) {
                Files.createDirectories(Paths.get(uploadPath));
            }
            String fileName = bookSlug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath, fileName);
            resourceURI = "/book_images/" + fileName;
            file.transferTo(path); // upload file
        }
        return resourceURI;
    }

    @BookFileInfo
    public Path getBookFilePath(String hash) {
        BookFile bookFile = bookFileRepository.findFileByHash(hash);
        return Paths.get(bookFile.getPath());
    }

    @BookFileInfo
    public MediaType getBookFileMime(String hash) {
        BookFile bookFile = bookFileRepository.findFileByHash(hash);
        String mimeType = URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null)
            return MediaType.parseMediaType(mimeType);
        else
            return MediaType.APPLICATION_OCTET_STREAM;
    }

    @BookFileInfo
    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFile bookFile = bookFileRepository.findFileByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        return Files.readAllBytes(path);
    }

}