package com.yapp.sharefood.tag.service;

import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.dto.TagDto;
import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.exception.TagConflictException;
import com.yapp.sharefood.tag.exception.TagNotFoundException;
import com.yapp.sharefood.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    public TagSearchResponse findSimilarTags(String similarTagName, int size) {
        List<Tag> similarTags = tagRepository.findSimilarTags(similarTagName, size);

        List<TagDto> tagDtos = similarTags.stream()
                .map(tag -> TagDto.of(tag.getId(), tag.getName()))
                .collect(Collectors.toList());

        return TagSearchResponse.of(tagDtos);
    }

    @Transactional
    public Tag saveTag(FoodTagDto tag) {
        if (!Objects.isNull(tag.getId())) {
            throw new TagConflictException();
        }

        return tagRepository.save(Tag.of(tag.getName()));
    }

    public List<Tag> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return tagRepository.findByIdIn(ids);
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(TagNotFoundException::new);
    }
}
