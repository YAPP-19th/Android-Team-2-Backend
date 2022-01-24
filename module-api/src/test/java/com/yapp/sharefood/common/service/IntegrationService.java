package com.yapp.sharefood.common.service;

import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.user.rand.UserNicknameRandomComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;

@SpringBootTest
public abstract class IntegrationService {

    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected TokenProvider tokenProvider;
    @MockBean
    protected UserNicknameRandomComponent userNicknameRandomComponent;


    @Autowired
    protected EntityManager entityManager;
}
