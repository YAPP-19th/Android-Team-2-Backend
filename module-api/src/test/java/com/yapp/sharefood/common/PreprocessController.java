package com.yapp.sharefood.common;

import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.common.documentation.DocumentRequestBuilder;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
public abstract class PreprocessController {

    @MockBean
    protected UserRepository userRepository;
    @MockBean
    protected TokenProvider tokenProvider;

    @Autowired
    protected MockMvc mockMvc;

    private DocumentRequestBuilder documentRequestBuilder;

    protected User user;

    protected Long authUserId = 1L;

    @BeforeEach
    void setUp() {

        documentRequestBuilder = new DocumentRequestBuilder();

        user = User.builder()
                .id(1L)
                .nickname("nickname")
                .oauthId("oauth_id")
                .oAuthType(OAuthType.KAKAO)
                .name("name")
                .build();

        willReturn(true)
                .given(tokenProvider).isValidToken(anyString());
        willReturn(1L)
                .given(tokenProvider).extractIdByToken(anyString());

        willReturn(Optional.of(user))
                .given(userRepository).findById(anyLong());
    }

    protected DocumentRequestBuilder.MockMvcFunction document() {
        return documentRequestBuilder.build().mockMvc(this.mockMvc);
    }
}
