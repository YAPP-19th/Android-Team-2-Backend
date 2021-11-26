package com.yapp.sharefood.tag.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.tag.dto.TagDto;
import com.yapp.sharefood.tag.dto.response.TagSearchResponse;
import com.yapp.sharefood.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class)
@DisplayName("tag controller 기능")
class TagControllerTest extends PreprocessController {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TagService tagService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("검색결과 tag가 없는 경우")
    void findSimilarTagNamesTest() throws Exception {
        // given
        willReturn(TagSearchResponse.of(new ArrayList<>()))
                .given(tagService).findSimilarTags(anyString(), anyInt());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/tags")
                .param("size", "10")
                .param("tagName", "tag"));

        // then
        TagSearchResponse tagSearchResponse = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(documentIdentify("tag/get/success/empty"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(0, tagSearchResponse.getTags().size());
    }

    @Test
    @DisplayName("10개 정도 tag 조회")
    void findSimliar10TagNameTest() throws Exception {
        // given
        List<TagDto> tags = Arrays.asList(TagDto.of(1L, "name1"),
                TagDto.of(2L, "name2"),
                TagDto.of(3L, "name3"),
                TagDto.of(4L, "name4"),
                TagDto.of(5L, "name5"),
                TagDto.of(6L, "name6"),
                TagDto.of(7L, "name7"),
                TagDto.of(8L, "name8"),
                TagDto.of(9L, "name9"),
                TagDto.of(10L, "name10"));
        willReturn(TagSearchResponse.of(tags))
                .given(tagService).findSimilarTags(anyString(), anyInt());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/tags")
                .param("size", "10")
                .param("tagName", "name"));

        // then
        TagSearchResponse tagSearchResponse = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(documentIdentify("tag/get/success/not_empty"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(10, tagSearchResponse.getTags().size());
        assertThat(tagSearchResponse.getTags())
                .extracting("name")
                .contains("name1", "name2", "name3", "name10")
                .doesNotContain("name11");
    }
}