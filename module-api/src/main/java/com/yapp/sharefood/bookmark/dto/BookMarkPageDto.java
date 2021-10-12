package com.yapp.sharefood.bookmark.dto;

import lombok.Data;

@Data
public class BookMarkPageDto {
    private Long id;
    private String title;
    private String categoryName;
    private int price;
    private String thumbnailUrl;
    private String mainFavorName;
    private boolean isBookmark;
}
