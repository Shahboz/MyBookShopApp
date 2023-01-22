package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.TagDto;
import com.example.MyBookShopApp.entity.Tag;
import com.example.MyBookShopApp.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TagServiceTest {

    private Tag tag;
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;

    @Autowired
    TagServiceTest(TagService service) {
        this.tagService = service;
    }

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1);
        tag.setName("Name");
        tag.setSlug("Slug");
    }

    @AfterEach
    void tearDown() {
        tag = null;
    }

    @Test
    void getTagDtoBySlug() {

        Mockito.doReturn(tag)
                .when(tagRepository)
                .findTagBySlug(tag.getSlug());

        TagDto dto = tagService.getTagDtoBySlug(tag.getSlug());

        assertNotNull(dto);
        assertEquals(dto.getSlug(), this.tag.getSlug());
        assertEquals(dto.getName(), this.tag.getName());
    }

    @Test
    void testEmptyGetTagDtoBySlug() {
        TagDto dto = tagService.getTagDtoBySlug("");

        assertNull(dto);
    }

}