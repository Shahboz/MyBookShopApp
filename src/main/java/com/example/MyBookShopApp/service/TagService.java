package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.TagRepository;
import com.example.MyBookShopApp.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public Map<Tag, Integer> getTags() {
        Map<Tag, Integer> tagBooks= new HashMap<>();
        List<Tag> tagList = tagRepository.findAll();
        tagList.forEach(tag -> {
            tagBooks.putIfAbsent(tag, 0);
            tagBooks.compute(tag, (key, value) -> value + tag.getBooks().size());
        });
        return tagBooks;
    }

}