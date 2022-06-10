package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.FileDownload;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileDownloadRepository extends JpaRepository<FileDownload, Integer> {

    FileDownload findFileDownloadByUserIdAndBookSlug(Integer userId, String bookSlug);

}