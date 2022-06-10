package com.example.MyBookShopApp.aspect;

import liquibase.util.file.FilenameUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;


@Aspect
@Component
public class BookFileInfoLogs {

    @Value("${upload.path}")
    private String uploadPath;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.BookFileInfo)")
    public void bookFileInfo() {}

    @Before("bookFileInfo() && args(file, bookSlug)")
    public void beforeSaveBookImage(MultipartFile file, String bookSlug) {
        if (!Files.exists(Paths.get(uploadPath)))
            logger.info("Directory " + uploadPath + " will be created.");
        else
            logger.info("BookFile " + file.getOriginalFilename() + " will be copied to directory " + uploadPath + ".");
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(file, bookSlug)")
    public void afterReturningCallSaveBookImage(MultipartFile file, String bookSlug) {
        logger.info( bookSlug + "." + FilenameUtils.getExtension(file.getOriginalFilename()) + " upload to " + uploadPath + ".");
    }


    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "path")
    public void afterReturningCallGetBookFilePath(String hash, Path path) {
        logger.info("Book (" + hash + ") file path: " + path);
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "mediaType")
    public void afterReturningCallGetBookFileMime(String hash, MediaType mediaType) {
        logger.info("Book (" + hash + ") file mime type: " + mediaType);
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "bytes")
    public void afterReturningCallGetBookByteArray(String hash, byte[] bytes) {
        logger.info("Book (" + hash + ") file data length: " + bytes.length);
    }

}