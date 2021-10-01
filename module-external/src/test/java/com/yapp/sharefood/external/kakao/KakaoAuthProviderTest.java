package com.yapp.sharefood.external.kakao;

import com.yapp.sharefood.external.OAuthProfile;
import com.yapp.sharefood.external.exception.BadGatewayException;
import io.netty.handler.timeout.ReadTimeoutException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest
class KakaoAuthProviderTest {
    private MockWebServer mockWebServer;

    @Autowired
    KakaoAuthProvider kakaoAuthProvider;
    @MockBean
    KakaoOAuthComponent kakaoOAuthComponent;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();

        willReturn(mockWebServer.url("/").toString())
                .given(kakaoOAuthComponent).oauthUrl();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("성공")
    void kakaoOuthRequest() throws Exception {
        // given
        String accessToken = "accestoken";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\n" +
                        "  \"id\": 1234,\n" +
                        "  \"connected_at\": \"2021-09-23T07:21:38Z\",\n" +
                        "  \"properties\": {\n" +
                        "    \"nickname\": \"kkh\"\n" +
                        "  },\n" +
                        "  \"kakao_account\": {\n" +
                        "    \"profile_nickname_needs_agreement\": false,\n" +
                        "    \"profile\": {\n" +
                        "      \"nickname\": \"kkh\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"));

        // when
        OAuthProfile oAuthProfileInfo = kakaoAuthProvider.getOAuthProfileInfo(accessToken);

        // then
        assertEquals("kkh", oAuthProfileInfo.oauthNickname());
    }

    @Test
    @DisplayName("Timeout 에러 발생 케이스")
    void kakaoOauthRequestTimeout() throws Exception {
        // given
        String accessToken = "accestoken";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBodyDelay(7000, TimeUnit.MILLISECONDS)
                .setBody("{\n" +
                        "  \"id\": 1234,\n" +
                        "  \"connected_at\": \"2021-09-23T07:21:38Z\",\n" +
                        "  \"properties\": {\n" +
                        "    \"nickname\": \"kkh\"\n" +
                        "  },\n" +
                        "  \"kakao_account\": {\n" +
                        "    \"profile_nickname_needs_agreement\": false,\n" +
                        "    \"profile\": {\n" +
                        "      \"nickname\": \"kkh\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"));

        // when

        // then
        assertThrows(ReadTimeoutException.class, () -> kakaoAuthProvider.getOAuthProfileInfo(accessToken));
    }

    @Test
    @DisplayName("500번 error 났을 경우")
    void gatewayErrorBy500Num() throws Exception {
        // given
        String accessToken = "accestoken";
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(""));

        // when

        // then
        assertThrows(BadGatewayException.class, () -> kakaoAuthProvider.getOAuthProfileInfo(accessToken));
    }

    @Test
    @DisplayName("400 error 났을 경우")
    void gatewayErrorBy400Num() throws Exception {
        // given
        String accessToken = "accestoken";
        mockWebServer.enqueue(new MockResponse().setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(""));

        // when

        // then
        assertThrows(BadGatewayException.class, () -> kakaoAuthProvider.getOAuthProfileInfo(accessToken));
    }
}