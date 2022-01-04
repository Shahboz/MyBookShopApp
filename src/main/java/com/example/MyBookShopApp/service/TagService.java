package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.TagRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository repository) {
        this.tagRepository = repository;
    }

    public Tag getTagBySlug(String slugTag) {
        return tagRepository.findTagBySlug(slugTag);
    }

    public Page<Book> getPageOfBooksByTag(String slugTag, int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return tagRepository.findBooksByTag(slugTag, nextPage);
    }

}
