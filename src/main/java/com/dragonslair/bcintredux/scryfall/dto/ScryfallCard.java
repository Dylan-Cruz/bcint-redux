package com.dragonslair.bcintredux.scryfall.dto;

import ch.qos.logback.core.Layout;
import com.dragonslair.bcintredux.enums.Rarity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
public class ScryfallCard {
    // sf fields
    private String id;
    private int tcgplayerId;
    private String scryfallUri;
    private boolean foil;
    private boolean nonfoil;
    private Layout layout;

    // card fields
    private String name;
    private String typeLine;
    private String oracleText;
    private String manaCost;
    private String power;
    private String toughness;
    private String loyalty;
    private ScryfallImageUris imageUris;
    private Rarity rarity;
    private String setName;
    private String set;
    private String collectorNumber;
    private List<CardFace> cardFaces;
    private ScryfallPrices prices;

    // print fields
    private LocalDate releasedAt;
}