package com.dragonslair.bcintredux.scryfall;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ScryfallService {

    @Autowired
    private WebClient scryfallWebClient;

    @Autowired
    private RateLimiter scryfallRateLimter;

    // get card by id

    // search cards

    // get all sets
}
