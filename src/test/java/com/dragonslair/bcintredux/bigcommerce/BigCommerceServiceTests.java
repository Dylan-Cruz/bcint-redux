package com.dragonslair.bcintredux.bigcommerce;

import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.google.common.util.concurrent.RateLimiter;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class BigCommerceServiceTests {

    private MockWebServer server;
    private BigCommerceService service;

    private String clientErrorBody = """
            {
                "status": 422,
                "title": "The filter: no is not a valid filter parameter.",
                "type": "https://developer.bigcommerce.com/api-docs/getting-started/api-status-codes"
            }
            """;

    private String emptyVariantBody = """
            {
                "data": [],
                "meta": {
                    "pagination": {
                        "total": 0,
                        "count": 0,
                        "per_page": 50,
                        "current_page": 1,
                        "total_pages": 0,
                        "links": {
                            "current": "?sku=MTGSAKH117RPL&page=1&limit=50"
                        }
                    }
                }
            }
            """;

    private String oneVariantBody = """
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

    private String twoVariantBody = """
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
                        },
                        {
                            "id": 341572,
                            "product_id": 117637,
                            "sku": "MTGSAKH117RPL",
                            "sku_id": 223629,
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
                                    "id": 223933,
                                    "label": "PL",
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

    @Test
    public void getVariantBySkuCallsAsExpected() throws InterruptedException {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(oneVariantBody);

        // enqueue the response
        server.enqueue(response);

        // make the call
        service.getVariantBySku("test-sku");

        // pop the call
        RecordedRequest request = server.takeRequest();

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertEquals("/bigcommerce/catalog/variants?sku=test-sku", request.getPath());
    }

    // test that an exception is thrown on an http error
    @Test
    public void getVariantsThrowsAsExpectedOnHttpError() {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .setBody(clientErrorBody);

        // enqueue the response
        server.enqueue(response);

        // validate
        BigCommerceServiceException exception = assertThrowsExactly(
            BigCommerceServiceException.class, () -> {
                service.getVariantBySku("test-sku");
            }
        );
        assertEquals("Error searching for variant with sku test-sku status code: 422 UNPROCESSABLE_ENTITY", exception.getMessage());
    }

    // test that a variant is returned when we get a 200 and one variant is present
    @Test
    public void getVariantReturnsAValidVariant() throws InterruptedException {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(oneVariantBody);

        // enqueue the response
        server.enqueue(response);

        // make the call
        Variant v = service.getVariantBySku("test-sku");

        assertEquals(341571, v.getId());
        assertEquals("MTGSAKH117RNM", v.getSku());
    }

    // test that an error is thrown when no variants are returned
    @Test
    public void getVariantsThrowsAsExpectedOnEmptyList() {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(emptyVariantBody);

        // enqueue the response
        server.enqueue(response);

        // make the call
        BigCommerceServiceException exception = assertThrowsExactly(
                BigCommerceServiceException.class, () -> {
                    service.getVariantBySku("test-sku");
                }
        );
        assertEquals("No variants exist with sku: test-sku", exception.getMessage());
    }


    // test that an error is thrown when more than one variant is returned
    @Test
    public void getVariantsThrowsAsExpectedOnTwoVariantList() {
        // verify the request is formed correctly
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(twoVariantBody);

        // enqueue the response
        server.enqueue(response);

        // make the call
        BigCommerceServiceException exception = assertThrowsExactly(
                BigCommerceServiceException.class, () -> {
                    service.getVariantBySku("test-sku");
                }
        );
        assertEquals("More than one variant exists with sku: test-sku", exception.getMessage());
    }
}
