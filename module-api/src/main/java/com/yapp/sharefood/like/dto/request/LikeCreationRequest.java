package com.yapp.sharefood.like.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCreationRequest {
    @NotNull
    @NotBlank
    private String categoryName;

    public static LikeCreationRequest of(String categoryName) {
        return new LikeCreationRequest(categoryName);
    }
}
