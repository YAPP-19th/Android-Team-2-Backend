package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.dto.request.FoodCreatRequest;
import com.yapp.sharefood.food.dto.request.FoodUpdateRequset;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.user.domain.User;
import io.swagger.annotations.*;
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
public class FoodController {
    @DeleteMapping("/api/v1/food/{id}")
    @ApiOperation("[auth] 내가 작성한 menu 삭제")
    @ApiResponses({
            @ApiResponse(code = 204, message = "[success] 선택한 menu 삭제"),
            @ApiResponse(code = 404, message = "[warn] 해당 menu를 찾지 못한 경우", response = HttpClientErrorException.NotFound.class),
            @ApiResponse(code = 403, message = "[error] 내가 작성한 menu가 아닌 경우", response = HttpClientErrorException.Forbidden.class)
    })
    public ResponseEntity<Void> deleteFoodById(@PathVariable Long id,
                                               @ApiIgnore @AuthUser User user) {
        // user not match exception -> forbidden
        // not found
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/food/{id}")
    @ApiOperation("[auth] 내가 작성한 menu 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 선택한 menu 수정"),
            @ApiResponse(code = 404, message = "[warn] 해당 menu를 찾지 못한 경우", response = HttpClientErrorException.NotFound.class),
            @ApiResponse(code = 403, message = "[error] 내가 작성한 menu가 아닌 경우", response = HttpClientErrorException.Forbidden.class)
    })
    public ResponseEntity<Void> editFoodById(@PathVariable Long id,
                                             @RequestBody FoodUpdateRequset foodUpdateRequset,
                                             @ApiIgnore @AuthUser User user) {

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/food")
    @ApiOperation("[auth] menu 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "[success] 메뉴 정보 등록", response = URI.class)
    })
    public ResponseEntity<URI> createFood(@ApiIgnore @AuthUser User user,
                                          @RequestBody FoodCreatRequest foodCreatRequest) {

        Long id = 1L;
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/api/v1/food/favorite/{type}")
    @ApiOperation("[auth] 최애 menu 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 카테고리별 최애 음식 리스트", response = FoodPageResponse.class),
            @ApiResponse(code = 400, message = "[error] 음료나 음식중 다른 값을 입력한 경우 발생", response = HttpClientErrorException.BadRequest.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "음식 음료 정보", required = true, paramType = "path")
    })
    public ResponseEntity<FoodPageResponse> findFavoriteFood(@ApiIgnore @AuthUser User user, @PathVariable("type") String categoryType) {
        FoodPageResponse response = new FoodPageResponse();
        return ResponseEntity.ok(response);
    }

    // category type에 맞는 음식/음료 추천하는 기능 고민 해보기
    @GetMapping("/api/v1/food/{type}/recommendation")
    @ApiOperation("[auth] 사용자가 선택했던 맛에 관련되 menu 정보 조회 -> 추천 기능")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 추천 menu 반환", response = FoodPageResponse.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page 번호", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "한번 pagination 조회 size 크기", defaultValue = "10", paramType = "query")
    })
    public ResponseEntity<FoodPageResponse> recommandedFood(@ApiIgnore @AuthUser User user,
                                                            @ApiIgnore Pageable pageable) {
        FoodPageResponse foodPageResponse = new FoodPageResponse();

        return ResponseEntity.ok(foodPageResponse);
    }

    @GetMapping("/api/v1/food/{type}/rank")
    @ApiOperation("category별 menu ranking 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] top랭킹 정보 반환", response = FoodPageResponse.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "음식 음료 정보", required = true, paramType = "path"),
            @ApiImplicitParam(name = "top", value = "랭킹 몇자리까지 가져올지 결정", defaultValue = "5", paramType = "query")
    })
    public ResponseEntity<TopRankFoodResponse> findTopRankingFood(@RequestParam(defaultValue = "5") int top, @PathVariable("type") String type) {
        TopRankFoodResponse topRankFoodResponse = new TopRankFoodResponse();
        return ResponseEntity.ok(topRankFoodResponse);
    }

    @GetMapping("/api/v1/food/{id}")
    @ApiOperation("menu detail 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 선택한 menu 반환", response = FoodDetailResponse.class),
            @ApiResponse(code = 404, message = "[warn] 해당 menu를 찾지 못한 경우", response = HttpClientErrorException.NotFound.class)
    })
    public ResponseEntity<FoodDetailResponse> findFoodDetailById(@PathVariable Long id) {
        FoodDetailResponse foodDetailResponse = new FoodDetailResponse();
        return ResponseEntity.ok(foodDetailResponse);
    }

    @GetMapping("/api/v1/food")
    @ApiOperation("게시판에 올라온 전체 menu 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 게시판에 올라온 menu 정보 조회", response = FoodPageResponse.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page 번호", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "한번 pagination 조회 size 크기", defaultValue = "10", paramType = "query")
    })
    public ResponseEntity<FoodPageResponse> findAllFoods(@ApiIgnore @PageableDefault(sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        FoodPageResponse foodPageResponse = new FoodPageResponse();

        return ResponseEntity.ok(foodPageResponse);
    }

    @GetMapping("/api/v1/food/me")
    @ApiOperation("[auth] 내가 등록한 menu 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[success] 내가 등록한 menu 반환", response = FoodPageResponse.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page 번호", defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "한번 pagination 조회 size 크기", defaultValue = "10", paramType = "query")
    })
    public ResponseEntity<FoodPageResponse> findMyFoods(@ApiIgnore @AuthUser User user,
                                                        @ApiIgnore @PageableDefault(sort = "lastModifiedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        FoodPageResponse foodPageResponse = new FoodPageResponse();

        return ResponseEntity.ok(foodPageResponse);
    }
}
