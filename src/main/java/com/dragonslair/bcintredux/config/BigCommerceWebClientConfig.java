package com.dragonslair.bcintredux.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class BigCommerceWebClientConfig {

    @Bean
    public WebClient bigCommerceWebClient(
        @Value("${dragonslair.bigcommerce.rooturi}") String rooturi,
        @Value("${dragonslair.bigcommerce.clientid}") String clientId,
        @Value("${dragonslair.bigcommerce.token}") String token
    ) {
        return WebClient.builder()
                .baseUrl(rooturi)
                //.defaultHeader("X-Auth-Client", clientId)
                .defaultHeader("X-Auth-Token", token)
                .defaultHeader("Accepts", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RateLimiter bigCommerceRateLimiter() { return RateLimiter.create(5.0, Duration.ofMinutes(1L)); }
}
