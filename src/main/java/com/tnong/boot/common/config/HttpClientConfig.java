package com.tnong.boot.common.config;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: suhe
 * @Date: 2025/12/29 14:46
 */
@Configuration
public class HttpClientConfig {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .build();

    @Bean
    public HttpClient httpClient() {
        return HTTP_CLIENT;
    }
}
