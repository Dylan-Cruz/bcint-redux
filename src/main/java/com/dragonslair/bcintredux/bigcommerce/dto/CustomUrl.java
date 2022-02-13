package com.dragonslair.bcintredux.bigcommerce.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class CustomUrl {
    private String url;
    private boolean isCustomized;
}