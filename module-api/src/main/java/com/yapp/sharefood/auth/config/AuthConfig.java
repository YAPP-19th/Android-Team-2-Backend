package com.yapp.sharefood.auth.config;

import com.yapp.sharefood.auth.token.JwtProviderStrategy;
import com.yapp.sharefood.auth.token.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public TokenProvider tokenProvider() {
        return new JwtProviderStrategy();
    }
}
