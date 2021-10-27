package com.yapp.sharefood.tag.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagDto {
    private Long id;
    private String name;

    public static TagDto of(Long id, String name) {
        return new TagDto(id, name);
    }
}
