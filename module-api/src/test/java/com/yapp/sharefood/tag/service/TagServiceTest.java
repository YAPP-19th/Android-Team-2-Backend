package com.yapp.sharefood.tag.service;

import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.exception.TagConflictException;
import com.yapp.sharefood.tag.exception.TagNotFoundException;
import com.yapp.sharefood.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
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

    @Test
    @DisplayName("tag save 성공")
    void saveTest() throws Exception {
        // given
        FoodTagDto newTag = FoodTagDto.of(null, "newTag", FoodIngredientType.ADD);

        // when
        Tag tag = tagService.saveTag(newTag);
        tagRepository.flush();
        Tag searchTag = tagRepository.findById(tag.getId())
                .orElseThrow();

        // then
        assertEquals(tag.getId(), searchTag.getId());
        assertEquals("newTag", searchTag.getName());
    }

    @Test
    @DisplayName("id 가 존재하는 food tag 정보 저장")
    void saveIdExistDtoExceptionTest() throws Exception {
        // given
        FoodTagDto newTag = FoodTagDto.of(1L, "newTag", FoodIngredientType.ADD);

        // when

        // then
        assertThrows(TagConflictException.class, () -> tagService.saveTag(newTag));
    }

    @Test
    void saveConflictException() throws Exception {
        // given
        tagRepository.save(Tag.of("newTag"));
        tagRepository.flush();
        FoodTagDto newTag = FoodTagDto.of(null, "newTag", FoodIngredientType.ADD);

        // when

        // then
        assertThrows(DataIntegrityViolationException.class, () -> tagService.saveTag(newTag));
    }

    @Test
    @DisplayName("tag Id로 tag 조회 기능 테스트")
    void findByIdsTest() throws Exception {
        // given
        List<Long> tagIds = new ArrayList<>();
        Tag tag1 = tagRepository.save(Tag.of("tag1"));
        Tag tag2 = tagRepository.save(Tag.of("tag2"));
        Tag tag3 = tagRepository.save(Tag.of("tag3"));
        Tag tag4 = tagRepository.save(Tag.of("tag4"));
        tagRepository.flush();
        tagIds.add(tag1.getId());
        tagIds.add(tag2.getId());
        tagIds.add(tag3.getId());
        tagIds.add(tag4.getId());

        // when
        List<Tag> tags = tagService.findByIds(tagIds);
        tags.sort(Comparator.comparingLong(Tag::getId));

        // then
        assertEquals(4, tags.size());
        assertThat(tags)
                .extracting("name")
                .contains("tag1", "tag2", "tag3", "tag4");
    }

    @Test
    @DisplayName("Id가 존재하지 않는 tag 조회 기능 테스트")
    void findByNotExistIdsTest() throws Exception {
        // given
        List<Long> ids = List.of(1L, 2L, 3L);

        // when
        List<Tag> tags = tagService.findByIds(ids);

        // then
        assertEquals(0, tags.size());
    }

    @Test
    @DisplayName("id 값이 없는 list로 tag 조회하기 테스트")
    void findEmptyTagTest() throws Exception {
        // given
        List<Long> emptyTagIds = new ArrayList<>();

        // when
        List<Tag> emptyTags = tagService.findByIds(emptyTagIds);

        // then
        assertEquals(0, emptyTags.size());
    }

    @Test
    @DisplayName("Null인 id list로 조회 테스트")
    void findNullListTagTest() throws Exception {
        // given
        List<Long> emptyTagIds = null;

        // when
        List<Tag> emptyTags = tagService.findByIds(emptyTagIds);

        // then
        assertEquals(0, emptyTags.size());
    }

    @Test
    @DisplayName("tag 이름으로 조회 기능 테스트")
    void findByNameTest() throws Exception {
        // given
        tagRepository.save(Tag.of("tag1"));
        tagRepository.flush();

        // when
        Tag tag = tagService.findByName("tag1");

        // then
        assertEquals("tag1", tag.getName());
    }

    @Test
    @DisplayName("name이 없는 tag 조회 에러 테스트")
    void notExistNameTest() throws Exception {
        // given

        // when

        // then
        assertThrows(TagNotFoundException.class, () -> tagService.findByName("name"));
    }
}