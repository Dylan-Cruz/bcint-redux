package com.dragonslair.bcintredux.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
public class ScryfallCardSearchResponse<T> extends ScryfallResponse<T> {
    private int totalCards;
}
