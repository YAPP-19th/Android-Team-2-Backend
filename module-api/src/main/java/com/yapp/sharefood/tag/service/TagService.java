package com.yapp.sharefood.tag.service;

import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.dto.TagDto;
import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}
