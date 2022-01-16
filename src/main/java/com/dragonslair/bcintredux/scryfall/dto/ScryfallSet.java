package com.dragonslair.bcintredux.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
public class ScryfallSet {
    private String id;
    private String code;
    private String name;
    private String uri;
    private String scryfallUri;
    private String searchUri;
    private LocalDate releasedAt;
    private String setType;
    private int cardCount;
    private boolean digital;
    private boolean nonfoilOnly;
    private boolean foilOnly;
    private String iconSvgUri;
}
