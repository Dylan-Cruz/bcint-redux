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

import java.util.List;

@Service
@Slf4j
public class BigCommerceService {

    private WebClient webClient;
    private RateLimiter rateLimiter;

    private int requestsRemaining = 1;
    private int timeToReset = 100;
    private int requestQuota = 10;
    private int timeWindow = 100;

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
    public Variant getVariantsBySku(String variantSku) {
        // get our variant
        List<Variant> variants = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/catalog/variants")
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
                .bodyToMono(new ParameterizedTypeReference<BcApiResponse<List<Variant>>>(){})
                .block()
                .getData();

        // see if we can return one variant
        if (variants.size() == 1) {
            return variants.stream().findFirst().get();
        }

        // we couldn't so throw an error
        if (variants.isEmpty()) {
            throw new BigCommerceServiceException("No variants exist with sku: " + variantSku);
        } else {
            throw new BigCommerceServiceException("More than one variant exists with sku: " + variantSku);
        }
    }
}
