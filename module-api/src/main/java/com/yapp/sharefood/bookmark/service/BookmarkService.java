package com.yapp.sharefood.bookmark.service;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import com.yapp.sharefood.bookmark.repository.BookmarkRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public Long saveBookmark(User user, Long foodId) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);

        Bookmark bookmark = Bookmark.of(findUser);
        findFood.assignBookmark(bookmark);
        bookmarkRepository.flush();

        return bookmark.getId();
    }

    @Transactional
    public void deleteBookmark(User user, Long foodId) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);

        findFood.deleteBookmark(findUser);
    }
}
