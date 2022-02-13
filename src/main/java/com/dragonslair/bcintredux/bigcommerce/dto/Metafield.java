package com.dragonslair.bcintredux.bigcommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class Metafield {
    private String permissionSet = "read";
    private String namespace = "com.dragonslair.dlbc";
    private String key;
    private String value;
    private String description;
    private String resourceType;
    private int resourceId;

    public Metafield(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Metafield() {

    }
}
