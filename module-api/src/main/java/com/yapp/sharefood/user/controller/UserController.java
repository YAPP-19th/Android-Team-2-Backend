package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponse;
import com.yapp.sharefood.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation("겹치지 않는 nickname 자동으로 생성하는 API")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 겹치지 않는 nickname 반환", response = UserNicknameResponse.class),
            @ApiResponse(code = 409, message = "[error] 자동화에서 nickname 겹치는 이슈 발생", response = HttpClientErrorException.Conflict.class)
    })
    @GetMapping("/api/v1/users/nickname")
    public ResponseEntity<UserNicknameResponse> findNotExistNickName() {
        String newNickname = userService.createUniqueNickname();
        UserNicknameResponse userNicknameResponse = new UserNicknameResponse(newNickname);
        return ResponseEntity.ok(userNicknameResponse);
    }

    @ApiOperation("[auth] 닉네임 중복 체크 API")
    @GetMapping("/api/v1/users/{userId}/nickname/validation")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 사용 가능한 닉네임입니다."),
            @ApiResponse(code = 409, message = "[error] 이미 사용중인 닉네임입니다.", response = HttpClientErrorException.Conflict.class)
    })
    public ResponseEntity<Void> checkNicknameDuplicate(@PathVariable("userId") Long userId, @RequestBody UserNicknameRequest request) {
        userService.checkNicknameDuplicate(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("[auth] 내 닉네임 변경 API")
    @PatchMapping("/api/v1/users/{userId}/nickname")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 닉네임 변경 성공"),
            @ApiResponse(code = 404, message = "[error] 해당 유저정보를 찾을 수 없습니다.", response = HttpClientErrorException.NotFound.class),
            @ApiResponse(code = 409, message = "[error] 이미 사용중인 닉네임입니다.", response = HttpClientErrorException.Conflict.class)
    })
    public ResponseEntity<UserNicknameResponse> updateNickname(@ApiIgnore @AuthUser User user, @PathVariable("userId") Long userId, @RequestBody UserNicknameRequest request) {
        UserNicknameResponse userNicknameResponse = new UserNicknameResponse();
        userNicknameResponse.setNickname(userService.changeUserNickname(userId, request));
        return ResponseEntity.ok(userNicknameResponse);
    }

    @ApiOperation("[auth] 내 유저정보 조회 API")
    @GetMapping("/api/v1/users/me")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 내 유저정보 조회 성공")
    })
    public ResponseEntity<MyUserInfoResponse> findMyUserInfo(@ApiIgnore @AuthUser User user) {
        MyUserInfoResponse response = new MyUserInfoResponse();
        response.setUserInfo(userService.findUserInfo(user.getId()));
        return ResponseEntity.ok(response);
    }

    @ApiOperation("[auth] 다른 유저정보 조회 API")
    @GetMapping("/api/v1/users/{userId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 유저정보 조회 성공"),
            @ApiResponse(code = 404, message = "[error] 해당 유저정보를 찾을 수 없습니다.", response = HttpClientErrorException.NotFound.class)
    })
    public ResponseEntity<UserInfoResponse> findUserInfo(@ApiIgnore @AuthUser User user, @PathVariable("userId") Long userId) {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserInfo(userService.findUserInfo(userId));
        return ResponseEntity.ok(response);
    }
}
