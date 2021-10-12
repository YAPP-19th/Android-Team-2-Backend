package com.yapp.sharefood.bookmark.controller;

import com.yapp.sharefood.bookmark.dto.BookMarkPageDto;
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

    @GetMapping("api/v1/food/bookmark")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] bookmark list 가져오기 성공")
    })
    public ResponseEntity<Page<BookMarkPageDto>> getBookmarkedFood(@PageableDefault(size = 10, sort = "lastModifiedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookMarkPageDto> response = Page.empty();
        return ResponseEntity.ok(response);
    }
}
