package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.FaqRepository;
import com.example.MyBookShopApp.entity.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class FaqService {

    private final FaqRepository faqRepository;

    @Autowired
    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAllFaqs() {
        return faqRepository.findAllByOrderBySortIndex();
    }

}