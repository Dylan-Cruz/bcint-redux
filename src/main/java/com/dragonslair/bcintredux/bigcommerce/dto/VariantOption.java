package com.dragonslair.bcintredux.bigcommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class VariantOption {
    @JsonIgnore
    private int id;
    @JsonIgnore
    private int productId;
    private String displayName;
    private String type;
    private int sortOrder;
    private List<OptionValue> optionValues;
    private String name;

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public int getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(int productId) {
        this.productId = productId;
    }
}