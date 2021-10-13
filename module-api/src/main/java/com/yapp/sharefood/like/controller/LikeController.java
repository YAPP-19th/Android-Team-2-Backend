package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.user.domain.User;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;

@RestController
public class LikeController {

    @PostMapping("/api/v1/food/{foodId}/like")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] like 추가 성공"),
            @ApiResponse(code = 409, message = "[error] 이미 like가 추가된 상태입니다.", response = HttpClientErrorException.Conflict.class)
    })
    public ResponseEntity<Void> createLike(@ApiIgnore @AuthUser User user, @PathVariable("foodId") Long foodId) {
        Long id = 1L;
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/api/v1/food/{foodId}/like")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] like 제거 성공"),
            @ApiResponse(code = 404, message = "[error] 삭제할 like가 없습니다.", response = HttpClientErrorException.NotFound.class)
    })
    public ResponseEntity<Void> deleteLike(@ApiIgnore @AuthUser User user, @PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok().build();
    }
}
