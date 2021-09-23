package com.yapp.sharefood.auth.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.AuthProvider;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class AuthenticationManager {
    private final Map<String, AuthProvider> authProviderMap;
}
