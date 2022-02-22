package com.dragonslair.bcintredux.bigcommerce;

import com.google.common.util.concurrent.RateLimiter;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

public class BigCommerceServiceTests {

    private MockWebServer server;
    private BigCommerceService service;

    private String jsonBody = """
        {
            "data": [
                {
                    "id": 341571,
                    "product_id": 117637,
                    "sku": "MTGSAKH117RNM",
                    "sku_id": 223628,
                    "price": null,
                    "calculated_price": 0,
                    "sale_price": null,
                    "retail_price": null,
                    "map_price": null,
                    "weight": null,
                    "calculated_weight": 0.06,
                    "width": null,
                    "height": null,
                    "depth": null,
                    "is_free_shipping": false,
                    "fixed_cost_shipping_price": null,
                    "purchasing_disabled": false,
                    "purchasing_disabled_message": "",
                    "image_url": "",
                    "cost_price": 0,
                    "upc": "",
                    "mpn": "",
                    "gtin": "",
                    "inventory_level": 0,
                    "inventory_warning_level": 0,
                    "bin_picking_number": "",
                    "option_values": [
                        {
                            "id": 223932,
                            "label": "NM",
                            "option_id": 111818,
                            "option_display_name": "Condition"
                        }
                    ]
                }
            ],
            "meta": {
                "pagination": {
                    "total": 1,
                    "count": 1,
                    "per_page": 50,
                    "current_page": 1,
                    "total_pages": 1,
                    "links": {
                        "current": "?sku=MTGSAKH117RNM&page=1&limit=50"
                    }
                }
            }
        }
    """;

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

    @AfterEach
    public void tearDown() throws IOException {
        server.shutdown();
    }

    // test that a GET request is made to the correct url and with the correct params
    @Test
    public void getVariantBySkuCallsAsExpected() {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(jsonBody);
    }


    // test that a variant is returned when we get a 200
    
    // test that an exception is thrown on an error

}
