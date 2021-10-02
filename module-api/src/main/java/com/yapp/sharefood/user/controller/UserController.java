package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.user.dto.response.UserNicknameResponseDto;
import com.yapp.sharefood.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation("겹치지 않는 nickname 자동으로 생성하는 API")
    @GetMapping("/api/v1/users/nickname")
    public ResponseEntity<UserNicknameResponseDto> findNotExistNickName() {
        String newNickname = userService.createUniqueNickname();
        UserNicknameResponseDto userNicknameResponseDto = new UserNicknameResponseDto(newNickname);

        return ResponseEntity.ok(userNicknameResponseDto);
    }
}
