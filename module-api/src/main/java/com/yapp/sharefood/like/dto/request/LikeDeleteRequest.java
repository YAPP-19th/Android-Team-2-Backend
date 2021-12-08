package com.yapp.sharefood.like.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeDeleteRequest {
    @NotNull
    @NotBlank
    private String categoryName;

    public static LikeDeleteRequest of(String categoryName) {
        return new LikeDeleteRequest(categoryName);
    }
}
