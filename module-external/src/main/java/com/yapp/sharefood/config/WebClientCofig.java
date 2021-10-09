package com.yapp.sharefood.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientCofig {
    private static final int TIMEOUT_TIME = 5_000;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_TIME)
                                .doOnConnected(connection -> connection.addHandler(new ReadTimeoutHandler(TIMEOUT_TIME, TimeUnit.MILLISECONDS)))
                )).build();
    }
}
