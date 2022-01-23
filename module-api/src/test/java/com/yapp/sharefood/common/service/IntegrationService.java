package com.yapp.sharefood.common.service;

import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public abstract class IntegrationService {

    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected TokenProvider tokenProvider;
}
