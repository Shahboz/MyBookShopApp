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
import java.util.logging.Level;
import java.util.logging.Logger;


@Aspect
@Component
public class BookFileInfoLogs {

    @Value("${upload.path}")
    private String uploadPath;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.BookFileInfo)")
    public void bookFileInfo() {
        // Do nothing
    }

    @Before("bookFileInfo() && args(file, bookSlug)")
    public void beforeSaveBookImage(MultipartFile file, String bookSlug) {
        if (!Files.exists(Paths.get(uploadPath)))
            logger.log(Level.INFO, "Directory {0} will be created.", uploadPath);
        else
            logger.log(Level.INFO, "BookFile {0} will be copied to directory {1}.", new String[]{file.getOriginalFilename(), uploadPath});
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(file, bookSlug)")
    public void afterReturningCallSaveBookImage(MultipartFile file, String bookSlug) {
        logger.log(Level.FINE, "{0}.{1} upload to {2}.", new String[]{bookSlug, FilenameUtils.getExtension(file.getOriginalFilename()), uploadPath});
    }


    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "path")
    public void afterReturningCallGetBookFilePath(String hash, Path path) {
        logger.log(Level.FINE, "Book ({0}) file path: {1}", new Object[]{hash, path.toAbsolutePath()});
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "mediaType")
    public void afterReturningCallGetBookFileMime(String hash, MediaType mediaType) {
        logger.log(Level.FINE, "Book ({0}) file mime type: {1}", new String[]{hash, mediaType.getType()});
    }

    @AfterReturning(pointcut = "bookFileInfo() && args(hash)", returning = "bytes")
    public void afterReturningCallGetBookByteArray(String hash, byte[] bytes) {
        logger.log(Level.FINE, "Book ({0}) file data length: {1}", new String[]{hash, String.valueOf(bytes.length)});
    }

}