package com.dragonslair.bcintredux.config;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceQuotaManager;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class BigCommerceWebClientConfig {

    @Bean
    public WebClient bigCommerceWebClient(
            @Value("${dragonslair.bigcommerce.rooturi}") String rooturi,
            @Value("${dragonslair.bigcommerce.clientid}") String clientId,
            @Value("${dragonslair.bigcommerce.token}") String token,
            @Autowired BigCommerceQuotaManager quotaManager,
            @Autowired RateLimiter bigCommerceRateLimiter
            ) {
        return WebClient.builder()
                .baseUrl(rooturi)
                //.defaultHeader("X-Auth-Client", clientId)
                .defaultHeader("X-Auth-Token", token)
                .defaultHeader("Accepts", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    bigCommerceRateLimiter.acquire();
                    quotaManager.blockForQuota(request);
                    return next.exchange(request);
                })
                .filter(ExchangeFilterFunction.ofResponseProcessor(response -> {
                    quotaManager.updateValues(response.headers().asHttpHeaders());
                    return Mono.just(response);
                }))
                .build();
    }

    @Bean
    public RateLimiter bigCommerceRateLimiter() { return RateLimiter.create(5.5, Duration.ofSeconds(15L)); }
}
