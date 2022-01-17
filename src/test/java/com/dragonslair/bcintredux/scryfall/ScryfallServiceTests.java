package com.dragonslair.bcintredux.scryfall;

import com.google.common.util.concurrent.RateLimiter;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ScryfallServiceTests {

    private MockWebServer server;
    private ScryfallService service;

    @BeforeEach
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/scryfall/");

        service = new ScryfallService(
                WebClient.builder().baseUrl(baseUrl.toString()).build(),
                RateLimiter.create(1000)
        );
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getCardByIdCallsAsExpected() throws ScryfallServiceException {
        // verify that the request destination ends as expected
        MockResponse response = new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody();

        //TODO json body

    }

    @Test
    public void getCardByIdWhen200ReturnCard() {

    }

    @Test
    public void getCardByIdWhenErrorThrowException() {

    }
}
