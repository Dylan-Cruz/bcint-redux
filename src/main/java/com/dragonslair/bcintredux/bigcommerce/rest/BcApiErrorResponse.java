package com.dragonslair.bcintredux.bigcommerce.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class BcApiErrorResponse {
    private String status;
    private String title;
    private String type;

    @Override
    public String toString() {
        return new StringBuilder()
            .append("status: ")
            .append(status)
            .append(" message: ")
            .append(title)
            .toString();
    }
}
