package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.GenreRepository;
import com.example.MyBookShopApp.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class GenreService {

    private GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getGenres() {
        List<Genre> genreList = genreRepository.findAll();
        for(Iterator<Genre> iterator = genreList.iterator(); iterator.hasNext();) {
            Genre genre = iterator.next();
            if(genre.getParent() != null)
                iterator.remove();
        }
        return genreList;
    }

    public Map<Genre, Integer> getGenreDeep() {
        Map<Genre, Integer> genreDeep = new HashMap<>();
        for (Genre genre : genreRepository.findAll()) {
            genreDeep.putIfAbsent(genre, genreRepository.findDeepByGenre(genre.getRoot().getId()));
        }
        return genreDeep;
    }

    public Genre getGenreBySlug(String genreSlug) {
        return genreRepository.findGenreBySlug(genreSlug);
    }

}