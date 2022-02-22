package com.dragonslair.bcintredux.bigcommerce.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Pagination {
    private int total;
    private int count;
    private int perPage;
    private int currentPage;
    private int totalPages;
    private Links links;
}