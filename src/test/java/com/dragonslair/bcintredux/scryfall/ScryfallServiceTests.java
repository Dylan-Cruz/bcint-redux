package com.dragonslair.bcintredux.scryfall;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ScryfallServiceTests {

    private MockWebServer server;
    private ScryfallService service;
    private String errorJsonBody = """
            {
              "object": "error",
              "code": "not_found",
              "status": 404,
              "details": "The requested object or REST method was not found."
            }""";
    private String jsonBody = """
            {
              "object": "card",
              "id": "e3285e6b-3e79-4d7c-bf96-d920f973b122",
              "oracle_id": "4457ed35-7c10-48c8-9776-456485fdf070",
              "multiverse_ids": [
                442130
              ],
              "mtgo_id": 67196,
              "mtgo_foil_id": 67197,
              "tcgplayer_id": 161480,
              "cardmarket_id": 319044,
              "name": "Lightning Bolt",
              "lang": "en",
              "released_at": "2018-03-16",
              "uri": "https://api.scryfall.com/cards/e3285e6b-3e79-4d7c-bf96-d920f973b122",
              "scryfall_uri": "https://scryfall.com/card/a25/141/lightning-bolt?utm_source=api",
              "layout": "normal",
              "highres_image": true,
              "image_status": "highres_scan",
              "image_uris": {
                "small": "https://c1.scryfall.com/file/scryfall-cards/small/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.jpg?1562442158",
                "normal": "https://c1.scryfall.com/file/scryfall-cards/normal/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.jpg?1562442158",
                "large": "https://c1.scryfall.com/file/scryfall-cards/large/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.jpg?1562442158",
                "png": "https://c1.scryfall.com/file/scryfall-cards/png/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.png?1562442158",
                "art_crop": "https://c1.scryfall.com/file/scryfall-cards/art_crop/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.jpg?1562442158",
                "border_crop": "https://c1.scryfall.com/file/scryfall-cards/border_crop/front/e/3/e3285e6b-3e79-4d7c-bf96-d920f973b122.jpg?1562442158"
              },
              "mana_cost": "{R}",
              "cmc": 1.0,
              "type_line": "Instant",
              "oracle_text": "Lightning Bolt deals 3 damage to any target.",
              "colors": [
                "R"
              ],
              "color_identity": [
                "R"
              ],
              "keywords": [
                        
              ],
              "legalities": {
                "standard": "not_legal",
                "future": "not_legal",
                "historic": "banned",
                "gladiator": "legal",
                "pioneer": "not_legal",
                "modern": "legal",
                "legacy": "legal",
                "pauper": "legal",
                "vintage": "legal",
                "penny": "not_legal",
                "commander": "legal",
                "brawl": "not_legal",
                "historicbrawl": "legal",
                "alchemy": "not_legal",
                "paupercommander": "legal",
                "duel": "legal",
                "oldschool": "not_legal",
                "premodern": "legal"
              },
              "games": [
                "paper",
                "mtgo"
              ],
              "reserved": false,
              "foil": true,
              "nonfoil": true,
              "finishes": [
                "nonfoil",
                "foil"
              ],
              "oversized": false,
              "promo": false,
              "reprint": true,
              "variation": false,
              "set_id": "41ee6e2f-69b3-4c53-8a8e-960f5e974cfc",
              "set": "a25",
              "set_name": "Masters 25",
              "set_type": "masters",
              "set_uri": "https://api.scryfall.com/sets/41ee6e2f-69b3-4c53-8a8e-960f5e974cfc",
              "set_search_uri": "https://api.scryfall.com/cards/search?order=set&q=e%3Aa25&unique=prints",
              "scryfall_set_uri": "https://scryfall.com/sets/a25?utm_source=api",
              "rulings_uri": "https://api.scryfall.com/cards/e3285e6b-3e79-4d7c-bf96-d920f973b122/rulings",
              "prints_search_uri": "https://api.scryfall.com/cards/search?order=released&q=oracleid%3A4457ed35-7c10-48c8-9776-456485fdf070&unique=prints",
              "collector_number": "141",
              "digital": false,
              "rarity": "uncommon",
              "watermark": "set",
              "flavor_text": "The sparkmage shrieked, calling on the rage of the storms of his youth. To his surprise, the sky responded with a fierce energy he'd never thought to see again.",
              "card_back_id": "0aeebaf5-8c7d-4636-9e82-8c27447861f7",
              "artist": "Christopher Moeller",
              "artist_ids": [
                "21e10012-06ae-44f2-b38d-3824dd2e73d4"
              ],
              "illustration_id": "013e7eda-ef8e-44cd-9832-4033d9de1c34",
              "border_color": "black",
              "frame": "2015",
              "full_art": false,
              "textless": false,
              "booster": true,
              "story_spotlight": false,
              "edhrec_rank": 429,
              "prices": {
                "usd": "2.54",
                "usd_foil": "4.26",
                "usd_etched": null,
                "eur": "2.64",
                "eur_foil": "4.00",
                "tix": "0.60"
              },
              "related_uris": {
                "gatherer": "https://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=442130",
                "tcgplayer_infinite_articles": "https://infinite.tcgplayer.com/search?contentMode=article&game=magic&partner=scryfall&q=Lightning+Bolt&utm_campaign=affiliate&utm_medium=api&utm_source=scryfall",
                "tcgplayer_infinite_decks": "https://infinite.tcgplayer.com/search?contentMode=deck&game=magic&partner=scryfall&q=Lightning+Bolt&utm_campaign=affiliate&utm_medium=api&utm_source=scryfall",
                "edhrec": "https://edhrec.com/route/?cc=Lightning+Bolt",
                "mtgtop8": "https://mtgtop8.com/search?MD_check=1&SB_check=1&cards=Lightning+Bolt"
              },
              "purchase_uris": {
                "tcgplayer": "https://shop.tcgplayer.com/product/productsearch?id=161480&utm_campaign=affiliate&utm_medium=api&utm_source=scryfall",
                "cardmarket": "https://www.cardmarket.com/en/Magic/Products/Search?referrer=scryfall&searchString=Lightning+Bolt&utm_campaign=card_prices&utm_medium=text&utm_source=scryfall",
                "cardhoarder": "https://www.cardhoarder.com/cards/67196?affiliate_id=scryfall&ref=card-profile&utm_campaign=affiliate&utm_medium=card&utm_source=scryfall"
              }
            }""";

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
    public void getCardByIdCallsAsExpected() throws ScryfallServiceException, InterruptedException {
        // verify that the request destination ends as expected
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(jsonBody);



        // enqueue the response
        server.enqueue(response);

        // make the call
        service.getCardById("test-id");

        // pop the call
        RecordedRequest request = server.takeRequest();

        assertEquals(request.getMethod(), HttpMethod.GET.name());
        assertEquals(request.getPath(), "/scryfall/cards/test-id");
    }

    @Test
    public void getCardByIdWhen200ReturnCard() throws ScryfallServiceException {
        // verify that the json deserializes
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(jsonBody);

        // enqueue the response
        server.enqueue(response);

        // validate the card is returned
        ScryfallCard card = service.getCardById("test-id");
        assertEquals(card.getId(), "e3285e6b-3e79-4d7c-bf96-d920f973b122");
    }

    @Test
    public void getCardByIdWhenErrorResponseThrowException() {
        // verify that when the api returns an error an exception is thrown
        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody(errorJsonBody);

        // enqueue the response
        server.enqueue(response);

        // make the call
        ScryfallServiceException exception = assertThrowsExactly(ScryfallServiceException.class, () -> {service.getCardById("test-id");});
        assertEquals("Error calling scryfall /cards/test-id status code: 404 NOT_FOUND", exception.getMessage());
    }
}
