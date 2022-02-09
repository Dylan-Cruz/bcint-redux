package com.dragonslair.bcintredux.scryfall;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScryfallService {

    private WebClient webClient;
    private RateLimiter rateLimiter;

    public ScryfallService(@Autowired WebClient scryfallWebClient, @Autowired RateLimiter scryfallRateLimiter) {
        webClient = scryfallWebClient;
        rateLimiter = scryfallRateLimiter;
    }

    // get card by id
    public ScryfallCard getCardById(String id) {
        rateLimiter.acquire(1);

        // configure the call
        return webClient
                .get()
                .uri("/cards/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                    Mono.error(new ScryfallServiceException("Error calling scryfall /cards/" + id + " status code: " + result.statusCode()))
                )
                .bodyToMono(ScryfallCard.class)
                .block();
    }

    // search cards

    // get all sets
}
