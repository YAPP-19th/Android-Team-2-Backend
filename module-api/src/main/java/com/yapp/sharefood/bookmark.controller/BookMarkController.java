package com.yapp.sharefood.bookmark.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class BookMarkController {

    @PostMapping("api/v1/food/{foodId}/bookmark/")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] bookmark 추가 성공"),
            @ApiResponse(code = 409, message = "[fail] 이미 추가된 bookmark가 존재합니다.", response = HttpClientErrorException.Conflict.class),
    })
    public ResponseEntity<Void> createBookMark(@PathVariable("foodId") Long foodId) {
        Long id = 1L;
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("api/v1/food/{foodId}/bookmark/")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] bookmark 삭제 성공"),
            @ApiResponse(code = 404, message = "[fail] 삭제할 bookmark가 없습니다.", response = HttpClientErrorException.NotFound.class),
    })
    public ResponseEntity<Void> deleteBookMark(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok().build();
    }
}
