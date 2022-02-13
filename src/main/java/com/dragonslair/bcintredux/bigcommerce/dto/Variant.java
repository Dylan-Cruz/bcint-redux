package com.dragonslair.bcintredux.bigcommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
public class Variant {
    private double price;
    private int inventoryLevel;
    private String sku;
    @JsonIgnore
    private int productId;
    private List<OptionValue> optionValues;

    @JsonIgnore
    private int id;

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

    @JsonProperty("product_id")
    public void setProductId(int productId) {
        this.productId = productId;
    }
}
