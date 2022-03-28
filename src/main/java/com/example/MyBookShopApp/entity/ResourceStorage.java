package com.example.MyBookShopApp.entity;

import com.example.MyBookShopApp.aspect.annotations.BookFileInfo;
import com.example.MyBookShopApp.dto.BookFileRepository;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ResourceStorage {

    @Value("${upload.path}")
    String uploadPath;

    @Value("${download.path}")
    String downloadPath;

    private final BookFileRepository bookFileRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    @BookFileInfo
    public String saveBookImage(MultipartFile file, String bookSlug) throws IOException {
        String resourceURI = null;
        if(!file.isEmpty()) {
            if(!new File(uploadPath).exists()) {
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
        BookFile bookFile = bookFileRepository.findBookFileByHash(hash);
        return Paths.get(bookFile.getPath());
    }

    @BookFileInfo
    public MediaType getBookFileMime(String hash) {
        BookFile bookFile = bookFileRepository.findBookFileByHash(hash);
        String mimeType = URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null)
            return MediaType.parseMediaType(mimeType);
        else
            return MediaType.APPLICATION_OCTET_STREAM;
    }

    @BookFileInfo
    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFile bookFile = bookFileRepository.findBookFileByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        return Files.readAllBytes(path);
    }

}