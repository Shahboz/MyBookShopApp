package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag findTagBySlug(String tagSlug);

    List<Tag> findAll();

}