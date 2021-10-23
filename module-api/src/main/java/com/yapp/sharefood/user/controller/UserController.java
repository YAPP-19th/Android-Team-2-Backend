package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.OtherUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponse;
import com.yapp.sharefood.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.yapp.sharefood.auth.utils.AuthValidationUtils.validateUserIdPath;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/users/nickname")
    public ResponseEntity<UserNicknameResponse> findNotExistNickName() {
        UserNicknameResponse response = userService.createUniqueNickname();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/users/{userId}/nickname/validation")
    public ResponseEntity<Void> checkNicknameDuplicate(@AuthUser User user,
                                                       @PathVariable("userId") Long userId,
                                                       @RequestParam(value = "nickname") String nickname) {
        validateUserIdPath(userId, user);

        userService.checkNicknameDuplicate(nickname);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/v1/users/{userId}/nickname")
    public ResponseEntity<UserNicknameResponse> updateNickname(@AuthUser User user,
                                                               @PathVariable("userId") Long userId,
                                                               @RequestBody UserNicknameRequest request) {
        validateUserIdPath(userId, user);

        UserNicknameResponse response = userService.changeUserNickname(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<MyUserInfoResponse> findMyUserInfo(@AuthUser User user) {
        MyUserInfoResponse response = userService.findUserInfo(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/users/{userId}")
    public ResponseEntity<OtherUserInfoResponse> findOtherUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findOtherUserInfo(userId));
    }
}
