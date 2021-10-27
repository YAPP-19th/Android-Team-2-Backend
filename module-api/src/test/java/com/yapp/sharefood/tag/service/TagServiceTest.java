package com.yapp.sharefood.tag.service;

import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("tag 데이터가 아무것도 없는 경우")
    void findSimilarTagEmptyDbTest() throws Exception {
        // given
        String simliarKeyword = "keyword";
        int size = 10;

        // when
        TagSearchResponse tagSearchResponse = tagService.findSimilarTags(simliarKeyword, size);

        // then
        assertEquals(0, tagSearchResponse.getTags().size());
    }

    @Test
    @DisplayName("1개이상 조회")
    void findSimilarTagsTest() {
        // given
        Tag tag1 = Tag.of("name_1");
        Tag tag2 = Tag.of("name_2");
        Tag tag3 = Tag.of("name_3");
        Tag tag4 = Tag.of("name_4");
        Tag tag5 = Tag.of("name_5");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);
        String simliarKeyword = "name";
        int size = 10;

        // when
        TagSearchResponse tagSearchResponse = tagService.findSimilarTags(simliarKeyword, size);

        // then
        assertEquals(5, tagSearchResponse.getTags().size());
        assertThat(tagSearchResponse.getTags())
                .extracting("name")
                .contains("name_1", "name_2", "name_3", "name_4", "name_5");
    }


    @Test
    @DisplayName("size개수 이상일 경우")
    void findSimilarTagsMoreThanSizeNumber() {
        // given
        Tag tag1 = Tag.of("name_1");
        Tag tag2 = Tag.of("name_2");
        Tag tag3 = Tag.of("name_3");
        Tag tag4 = Tag.of("name_4");
        Tag tag5 = Tag.of("name_5");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);
        String simliarKeyword = "name";
        int size = 2;

        // when
        TagSearchResponse tagSearchResponse = tagService.findSimilarTags(simliarKeyword, size);

        // then
        assertEquals(2, tagSearchResponse.getTags().size());
        assertThat(tagSearchResponse.getTags())
                .extracting("name")
                .contains("name_1", "name_2");
    }
}