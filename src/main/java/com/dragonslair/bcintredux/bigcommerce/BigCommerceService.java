package com.dragonslair.bcintredux.bigcommerce;

import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.bigcommerce.rest.BcApiResponse;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BigCommerceService {

    private WebClient webClient;
    private RateLimiter rateLimiter;

    public BigCommerceService(
        @Autowired WebClient bigCommerceWebClient,
        @Autowired RateLimiter bigCommerceRateLimiter
    ) {
        webClient = bigCommerceWebClient;
        rateLimiter = bigCommerceRateLimiter;
    }

    /**
     * Returns a big commerce variant by sku
     * @param variantSku
     * @return Variant
     */
    public Variant getVariantBySku(String variantSku) {
        rateLimiter.acquire(1);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("sku", variantSku)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        Mono.error(new BigCommerceServiceException("Error searching for variant with sku "
                                + variantSku
                                + " status code: "
                                + result.statusCode()))
                )
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<Variant>>(){})
                .block()
                .getData();
    }
}
