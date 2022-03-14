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
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

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
        return getCards(uriBuilder -> uriBuilder
                .path("/cards/search")
                .queryParams(params)
                .build())
                .expand(response -> {
                    String next = response.getNextPage();
                    if (next == null) {
                        return Mono.empty();
                    }
                    return getCards(uriBuilder -> uriBuilder.replacePath(next.substring(0, next.indexOf("?")))
                            .replaceQuery(next.substring(next.lastIndexOf("?")+1))
                            .build());
                }).flatMap(response -> Flux.fromIterable(response.getData()))
                .collectList()
                .block();
    }

    private Mono<ScryfallResponse<List<ScryfallCard>>> getCards(Function<UriBuilder, URI> uriFunction) {
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatus::isError, result ->
                        Mono.error(new ScryfallServiceException("Error calling scryfall /cards/search status code: " + result.statusCode()))
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
