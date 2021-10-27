package com.yapp.sharefood.tag.dto.response;

import com.yapp.sharefood.tag.dto.TagDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagSearchResponse {
    private List<TagDto> tags;

    public static TagSearchResponse of(List<TagDto> tags) {
        return new TagSearchResponse(tags);
    }
}
