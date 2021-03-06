package com.yapp.sharefood.bookmark.service;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import com.yapp.sharefood.bookmark.repository.BookmarkRepository;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.service.IntegrationService;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
class BookmarkServiceTest extends IntegrationService {

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    private Category saveTestCategory(String categoryName) {
        Category category = Category.of(categoryName);
        return categoryRepository.save(category);
    }

    private User saveTestUser(String nickname, String name, String oauthId) {
        User user = User.builder()
                .nickname(nickname)
                .name(name)
                .oAuthType(OAuthType.KAKAO)
                .oauthId(oauthId)
                .build();
        return userRepository.save(user);
    }

    private Food saveTestFood(String title, User user, Category category, FoodStatus foodStatus) {
        Food food = Food.builder()
                .foodTitle(title)
                .writer(user)
                .foodStatus(foodStatus)
                .category(category)
                .build();

        return foodRepository.save(food);
    }

    @DisplayName("????????? ?????? ??????")
    @Test
    void saveBookmarkTest_Success() {
        //given
        Category saveCategory = saveTestCategory("A");

        User writerUser = saveTestUser("user1_nick", "user1_name", "oauthId1");
        User user = saveTestUser("user2_nick", "user2_name", "oauthId2");

        Food food = saveTestFood("food title1", writerUser, saveCategory, FoodStatus.SHARED);

        //when
        Long bookmarkId = bookmarkService.saveBookmark(user, food.getId());
        Bookmark bookmark = bookmarkRepository.getById(bookmarkId);

        //then
        Long findBookmarkId = bookmark.getId();
        assertEquals(bookmarkId, findBookmarkId);

        User findBookmarkOwnerUser = bookmark.getUser();
        assertEquals(user, findBookmarkOwnerUser);
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ?????? ???????????? ????????? ????????? ??????")
    void saveBookmarkTest_Fail_IsMine() {
        //given
        Category saveCategory = saveTestCategory("A");

        User writerUser = saveTestUser("user1_nick", "user1_name", "oauthId1");

        Food food = saveTestFood("food title1", writerUser, saveCategory, FoodStatus.MINE);
        //when

        //then
        assertThrows(InvalidOperationException.class, () -> bookmarkService.saveBookmark(writerUser, food.getId()));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ?????????")
    void saveBookmarkTest_Fail_UserNotFound() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> bookmarkService.saveBookmark(User.builder().id(-1L).build(), 1L));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????(?????????)")
    void saveBookmarkTest_Fail_FoodNotFound() {
        //given
        User user = saveTestUser("user1_nick", "user1_name", "oauthId1");

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> bookmarkService.saveBookmark(user, -1L));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void deleteBookmarkTest_Success() {
        //given
        Category saveCategory = saveTestCategory("A");

        User user = saveTestUser("user1_nick", "user1_name", "oauthId1");

        Food food = saveTestFood("food title1", user, saveCategory, FoodStatus.SHARED);

        Bookmark savedBookmark = Bookmark.of(user);

        food.assignBookmark(savedBookmark);
        bookmarkRepository.flush();

        //when
        bookmarkService.deleteBookmark(user, food.getId());

        //then
        assertEquals(0, food.getBookmarks().getSize());
    }

    @DisplayName("????????? ?????? ?????? - ?????? ???????????? ????????? ??????")
    @Test
    void deleteBookmarkTest_Fail_BookmarkNotFound() {
        //given
        Category saveCategory = saveTestCategory("A");

        User user = saveTestUser("user1_nick", "user1_name", "oauthId1");

        Food food = saveTestFood("food title1", user, saveCategory, FoodStatus.SHARED);

        //when


        //then
        assertThrows(BookmarkNotFoundException.class, () -> bookmarkService.deleteBookmark(user, food.getId()));
    }

    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????(?????????)")
    @Test
    void deleteBookmarkTest_Fail_UserNotFound() {
        //given

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> bookmarkService.deleteBookmark(User.builder().id(1L).build(), -1L));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????(?????????)")
    void deleteBookmarkTest_Fail_FoodNotFound() {
        //given
        User user = saveTestUser("user1_nick", "user1_name", "oauthId1");

        //when

        //then
        assertThrows(FoodNotFoundException.class, () -> bookmarkService.deleteBookmark(user, -1L));
    }
}
