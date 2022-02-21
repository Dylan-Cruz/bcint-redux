package com.dragonslair.bcintredux.bigcommerce;

import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.google.common.util.concurrent.RateLimiter;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

public class BigCommerceServiceTests {

    private MockWebServer server;
    private BigCommerceService service;

    @BeforeEach
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/bigcommerce/");

        service = new BigCommerceService(
                WebClient.builder().baseUrl(baseUrl.toString()).build(),
                RateLimiter.create(1000)
        );
    }
}
