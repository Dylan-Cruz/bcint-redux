package com.dragonslair.bcintredux.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
public class CardFace {
    private String name;
    private String manaCost;
    private String oracleText;
    private String power;
    private String toughness;
    private String typeLine;
    private String loyalty;
    private ScryfallImageUris imageUris;
}
