package com.dragonslair.bcintredux.bigcommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class ProductImage {
    private boolean isThumbnail;
    private int sortOrder;
    private String description;
    @JsonIgnore
    private int id;
    @JsonIgnore
    private int productId;
    private String imageFile;
    private String urlZoom;
    private String urlStandard;
    private String urlThumbnail;
    private String urlTiny;
    private String imageUrl;

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
    public void setproductId(int productId) {
        this.productId = productId;
    }
}
