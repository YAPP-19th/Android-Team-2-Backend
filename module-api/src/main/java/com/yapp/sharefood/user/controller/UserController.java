package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.request.NicknameRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponseDto;
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
            @ApiResponse(code = 200, message = "[success] 겹치지 않는 nickname 반환", response = UserNicknameResponseDto.class),
            @ApiResponse(code = 409, message = "[error] 자동화에서 nickname 겹치는 이슈 발생", response = HttpClientErrorException.Conflict.class)
    })
    @GetMapping("/api/v1/users/nickname")
    public ResponseEntity<UserNicknameResponseDto> findNotExistNickName() {
        String newNickname = userService.createUniqueNickname();
        UserNicknameResponseDto userNicknameResponseDto = new UserNicknameResponseDto(newNickname);
        userNicknameResponseDto.setNickname("yumyum");
        return ResponseEntity.ok(userNicknameResponseDto);
    }

    @ApiOperation("닉네임 중복 체크 API")
    @GetMapping("/api/v1/users/{userId}/nickname/validation")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 사용 가능한 닉네임입니다."),
            @ApiResponse(code = 409, message = "[error] 이미 사용중인 닉네임입니다.", response = HttpClientErrorException.Conflict.class)
    })
    public ResponseEntity<Void> checkNicknameDuplicate(@PathVariable("userId") Long userId, @RequestBody NicknameRequest request) {
        return ResponseEntity.ok().build();
    }

    @ApiOperation("내 닉네임 변경 API")
    @PatchMapping("/api/v1/users/{userId}/nickname")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 닉네임 변경 성공"),
            @ApiResponse(code = 404, message = "[error] 해당 유저정보를 찾을 수 없습니다.", response = HttpClientErrorException.NotFound.class),
            @ApiResponse(code = 409, message = "[error] 이미 사용중인 닉네임입니다.", response = HttpClientErrorException.Conflict.class)
    })
    public ResponseEntity<MyUserInfoResponse> updateNickname(@ApiIgnore @AuthUser User user, @PathVariable("userId") Long userID, @RequestBody NicknameRequest request) {
        MyUserInfoResponse response = new MyUserInfoResponse();
        response.setNickName("yumyum");
        return ResponseEntity.ok(response);
    }

    @ApiOperation("내 유저정보 조회 API")
    @GetMapping("/api/v1/users/me")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 내 유저정보 조회 성공")
    })
    public ResponseEntity<MyUserInfoResponse> findMyUserInfo(@ApiIgnore @AuthUser User user) {
        MyUserInfoResponse response = new MyUserInfoResponse();
        response.setNickName("yumyum");
        return ResponseEntity.ok(response);
    }

    @ApiOperation("다른 유저정보 조회 API")
    @GetMapping("/api/v1/users/{userId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 유저정보 조회 성공"),
            @ApiResponse(code = 404, message = "[error] 해당 유저정보를 찾을 수 없습니다.", response = HttpClientErrorException.NotFound.class)
    })
    public ResponseEntity<UserInfoResponse> findUserInfo(@ApiIgnore @AuthUser User user, @PathVariable("userId") Long userId) {
        UserInfoResponse response = new UserInfoResponse();
        response.setNickName("yumyum");
        return ResponseEntity.ok(response);
    }
}
