package com.yapp.sharefood.bookmark.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class BookMarkController {

    @PostMapping("api/v1/food/{foodId}/bookmark/")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] bookmark 추가 성공"),
            @ApiResponse(code = 409, message = "[fail] 이미 bookmark 추가 됨", response = HttpClientErrorException.Conflict.class),
    })
    public ResponseEntity<Void> createBookMark(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("api/v1/food/{foodId}/bookmark/")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] bookmark 삭제 성공"),
            @ApiResponse(code = 404, message = "[fail] 삭제할 bookmark 없음", response = HttpClientErrorException.NotFound.class),
    })
    public ResponseEntity<Void> deleteBookMark(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok().build();
    }
}
