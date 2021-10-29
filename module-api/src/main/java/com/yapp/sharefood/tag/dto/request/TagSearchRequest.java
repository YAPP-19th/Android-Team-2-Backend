package com.yapp.sharefood.tag.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class TagSearchRequest {
    @NotNull
    @NotBlank
    private String tagName;

    @Max(20)
    private int size;
}
