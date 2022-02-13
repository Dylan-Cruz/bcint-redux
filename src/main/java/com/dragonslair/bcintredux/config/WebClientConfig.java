package com.dragonslair.bcintredux.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    // Scryfall config
    @Bean
    public WebClient getScryfallWebClient(@Value("dragonslair.scryfall.baseUrl") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RateLimiter getScryfallRateLimiter() {
        return RateLimiter.create(9, 60l, TimeUnit.SECONDS);
    }

    @Bean
    public WebClient getBigCommerceWebClient(
        @Value("dragonslair.bigcommerce.rooturi") String baseUrl,
        @Value("dragonslair.bigcommerce.clientid") String clientId,
        @Value("dragonslair.bigcommerce.token") String token
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Client", clientId)
                .defaultHeader("X-Auth-Token", token)
                .defaultHeader("Accepts", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RateLimiter getBigCommerceRateLimiter() { return RateLimiter.create(1000.0, Duration.ofMinutes(1L)); }
}
