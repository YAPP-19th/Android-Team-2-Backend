package com.yapp.sharefood.tag.controller;

import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/api/v1/tags")
    public ResponseEntity<TagSearchResponse> findSimilarTagNames(@RequestParam(name = "tagName") String tagName,
                                                                 @RequestParam(name = "size") int size) {
        return ResponseEntity.ok(tagService.findSimilarTags(tagName, size));
    }
}
