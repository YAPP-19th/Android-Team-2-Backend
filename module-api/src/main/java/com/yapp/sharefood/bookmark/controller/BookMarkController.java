package com.yapp.sharefood.bookmark.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.bookmark.dto.BookMarkPageDto;
import com.yapp.sharefood.user.domain.User;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;

@RestController
public class BookMarkController {

    @PostMapping("/api/v1/food/{foodId}/bookmark")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] bookmark 추가 성공"),
            @ApiResponse(code = 409, message = "[fail] 이미 추가된 bookmark가 존재합니다.", response = HttpClientErrorException.Conflict.class),
    })
    public ResponseEntity<Void> createBookMark(@ApiIgnore @AuthUser User user, @PathVariable("foodId") Long foodId) {
        Long id = 1L;
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/api/v1/food/{foodId}/bookmark")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] bookmark 삭제 성공"),
            @ApiResponse(code = 404, message = "[fail] 삭제할 bookmark가 없습니다.", response = HttpClientErrorException.NotFound.class),
    })
    public ResponseEntity<Void> deleteBookMark(@ApiIgnore @AuthUser User user, @PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok().build();
    }
}
