package com.dragonslair.bcintredux.scryfall;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallResponse;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallSet;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    public List<ScryfallCard> searchCards(MultiValueMap<String, String> params) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.scryfall.com/cards/search")
                .queryParams(params)
                .build()
                .toUri();

        return getCards(uri.toString())
                .expand(response -> {
                    String next = response.getNextPage();
                    if (next == null) {
                        return Mono.empty();
                    }
                    return getCards(UriUtils.decode(next, StandardCharsets.UTF_8));
                }).flatMap(response -> Flux.fromIterable(response.getData()))
                .collectList()
                .block();
    }

    private Mono<ScryfallResponse<List<ScryfallCard>>> getCards(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        Mono.error(new ScryfallServiceException("Error calling scryfall uri: " + uri + " status code: " + result.statusCode()))
                ).bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    // get all sets
    public List<ScryfallSet> getAllSets() {
        return webClient.get()
                .uri("/sets/")
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        Mono.error(new ScryfallServiceException("Error calling scryfall /sets/ status code: " + result.statusCode()))
                )
                .bodyToMono(new ParameterizedTypeReference<ScryfallResponse<List<ScryfallSet>>>() {
                })
                .block()
                .getData();
    }
}
