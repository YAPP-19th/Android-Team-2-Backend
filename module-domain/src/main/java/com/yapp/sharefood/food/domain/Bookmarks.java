package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmarks {

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public int getSize() {
        return bookmarks.size();
    }

    public void assignBookmark(Food food, Bookmark bookmark) {
        if (Objects.isNull(bookmark)) {
            throw new BookmarkNotFoundException();
        }

        validateAlreadyBookmarkUser(bookmark.getId());

        bookmark.assignFood(food);
        if(!bookmarks.contains(bookmark)) {
            bookmarks.add(bookmark);
        }
    }

    public void deleteBookmark(Long userId) {
        Bookmark bookmark = findBookmarkByUserId(userId);
        bookmarks.remove(bookmark);
    }

    private Bookmark findBookmarkByUserId(Long userId) {
        return this.bookmarks.stream()
                .filter(bookmark -> bookmark.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow();
    }

    private void validateAlreadyBookmarkUser(Long userId) {
        if (isAlreadyBookmark(userId)) {
            throw new InvalidOperationException("이미 bookmark한 사용자 입니다.");
        }
    }

    private boolean isAlreadyBookmark(Long userId) {
        return bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getUser().getId().equals(userId));
    }
}
