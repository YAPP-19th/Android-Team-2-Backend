package com.yapp.sharefood.favorite.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FavoriteCreationRequest {
    @NotNull
    @NotEmpty
    private String categoryName;

    public static FavoriteCreationRequest of(String categoryName) {
        return new FavoriteCreationRequest(categoryName);
    }
}
