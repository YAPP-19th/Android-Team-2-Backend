package com.yapp.sharefood.flavor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.service.FlavorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlavorController.class)
class FlavorControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    FlavorService flavorService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("전체 정상 조회")
    void findAllFlavorsTest() throws Exception {
        // given
        willReturn(new FlavorsResponse(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.SPICY))))
                .given(flavorService).findAllFlavors();

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/flavors"));

        // then
        FlavorsResponse flavors = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(documentIdentify("flavor/get/success/notEmpty"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(2, flavors.getFlavors().size());
        assertThat(flavors.getFlavors())
                .extracting("flavorName")
                .contains(FlavorType.SWEET.getFlavorName(), FlavorType.SPICY.getFlavorName());
    }

    @Test
    @DisplayName("아무 것도 없는 경우")
    void findAllFlavorsAndEmptyTest() throws Exception {
        // given
        willReturn(new FlavorsResponse(List.of()))
                .given(flavorService).findAllFlavors();

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/flavors"));

        // then
        FlavorsResponse flavors = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(documentIdentify("flavor/get/success/empty"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(0, flavors.getFlavors().size());
    }
}