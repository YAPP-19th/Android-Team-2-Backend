package com.yapp.sharefood.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.auth.controller.AuthController;
import com.yapp.sharefood.auth.service.AuthService;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.bookmark.controller.BookmarkController;
import com.yapp.sharefood.bookmark.service.BookmarkService;
import com.yapp.sharefood.category.controller.CategoryController;
import com.yapp.sharefood.category.service.CategoryService;
import com.yapp.sharefood.favorite.controller.FavoriteController;
import com.yapp.sharefood.favorite.service.FavoriteService;
import com.yapp.sharefood.flavor.controller.FlavorController;
import com.yapp.sharefood.flavor.service.FlavorService;
import com.yapp.sharefood.food.controller.FoodController;
import com.yapp.sharefood.food.controller.FoodReportController;
import com.yapp.sharefood.food.controller.FoodSaveController;
import com.yapp.sharefood.food.facade.FoodSaveFacade;
import com.yapp.sharefood.food.service.FoodImageService;
import com.yapp.sharefood.food.service.FoodReportService;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.like.controller.LikeController;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.tag.controller.TagController;
import com.yapp.sharefood.tag.service.TagService;
import com.yapp.sharefood.user.controller.UserController;
import com.yapp.sharefood.user.controller.UserFlavorController;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.user.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;

@AutoConfigureRestDocs
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(controllers = {
        AuthController.class,
        UserController.class,
        UserFlavorController.class,
        BookmarkController.class,
        CategoryController.class,
        FavoriteController.class,
        FlavorController.class,
        FoodController.class,
        FoodReportController.class,
        FoodSaveController.class,
        LikeController.class,
        TagController.class
})
public abstract class PreprocessController {
    @Autowired
    protected MockMvc mockMvc;

    // for auth interceptor
    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected TokenProvider tokenProvider;

    // service
    @MockBean
    protected AuthService authService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected BookmarkService bookmarkService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected FavoriteService favoriteService;

    @MockBean
    protected FlavorService flavorService;

    @MockBean
    protected FoodService foodService;

    @MockBean
    protected FoodImageService foodImageService;

    @MockBean
    protected FoodReportService foodReportService;

    @MockBean
    protected FoodSaveFacade foodSaveFacade;

    @MockBean
    protected LikeService likeService;

    @MockBean
    protected TagService tagService;

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected User loginUser;

    protected Long loginUserId = 1L;

    protected void loginMockSetup() {
        loginUser = User.builder()
                .id(loginUserId)
                .nickname("nickname")
                .oauthId("oauth_id")
                .oAuthType(OAuthType.KAKAO)
                .name("name")
                .build();

        willReturn(true).given(tokenProvider).isValidToken(anyString());
        willReturn(loginUserId).given(tokenProvider).extractIdByToken(anyString());
        willReturn(Optional.of(loginUser)).given(userRepository).findById(anyLong());
    }
}
