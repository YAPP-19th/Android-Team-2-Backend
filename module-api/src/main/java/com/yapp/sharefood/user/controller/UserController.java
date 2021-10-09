package com.yapp.sharefood.user.controller;

import com.yapp.sharefood.user.dto.response.UserNicknameResponseDto;
import com.yapp.sharefood.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

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

        return ResponseEntity.ok(userNicknameResponseDto);
    }
}
