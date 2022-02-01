package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.food.domain.FoodReportType;
import com.yapp.sharefood.food.dto.request.FoodReportRequest;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.report.exception.ReportNotDefineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FoodReportControllerTest extends PreprocessController {
    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @Test
    @DisplayName("신고 성공")
    void createReport_Success() throws Exception {
        //given
        FoodReportRequest request = FoodReportRequest.builder().foodReportMessage(FoodReportType.POSTING_ETC_CONTENT.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("food-report/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("신고 실패 - 존재하지 않는 음식(게시글)")
    void createReport_Fail_Food_Not_Found() throws Exception {
        //given
        willThrow(new FoodNotFoundException())
                .given(foodReportService).createReport(anyLong(), any(FoodReportRequest.class));
        FoodReportRequest request = FoodReportRequest.builder().foodReportMessage(FoodReportType.POSTING_ETC_CONTENT.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-report/post/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("신고 실패 - 존재하지 않는 작성자")
    void createReport_Fail_User_Not_Found() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(foodReportService).createReport(anyLong(), any(FoodReportRequest.class));
        FoodReportRequest request = FoodReportRequest.builder().foodReportMessage(FoodReportType.POSTING_ETC_CONTENT.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-report/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("신고 실패 - 존재하지 않는 신고 사유")
    void createReport_Fail_Report_Not_Define() throws Exception {
        //given
        willThrow(new ReportNotDefineException())
                .given(foodReportService).createReport(anyLong(), any(FoodReportRequest.class));
        FoodReportRequest request = FoodReportRequest.builder().foodReportMessage("a").build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-report/post/fail/reportNotDefine"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}