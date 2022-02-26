package com.dragonslair.bcintredux.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class ScryfallWebClientConfig {
    @Bean
    public WebClient scryfallWebClient(@Value("${dragonslair.scryfall.baseUrl}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RateLimiter scryfallRateLimiter() {
        return RateLimiter.create(9.0, 60l, TimeUnit.SECONDS);
    }

}
