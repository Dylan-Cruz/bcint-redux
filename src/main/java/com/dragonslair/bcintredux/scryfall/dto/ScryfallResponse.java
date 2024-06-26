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
public class ScryfallResponse<T> {
    private String object;
    private boolean hasMore;
    private String nextPage;
    private T data;
}
