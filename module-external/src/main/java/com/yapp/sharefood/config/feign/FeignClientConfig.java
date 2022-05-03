package com.yapp.sharefood.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.yapp.sharefood")
public class FeignClientConfig {
}
