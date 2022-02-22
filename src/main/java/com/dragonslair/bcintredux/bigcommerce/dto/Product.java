package com.dragonslair.bcintredux.bigcommerce.dto;

import com.dragonslair.bcintredux.bigcommerce.rest.BigCommerceDateSerializer;
import com.dragonslair.bcintredux.bigcommerce.enums.Availability;
import com.dragonslair.bcintredux.bigcommerce.enums.InventoryTracking;
import com.dragonslair.bcintredux.bigcommerce.enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
public class Product {

    @JsonIgnore
    private int id;
    private String name;
    private Type type; //physical, digital
    private String sku;
    private String description;
    @JsonInclude
    private double price;
    private double weight;
    private double depth;
    private double height;
    private double width;
    private List<Integer> categories;
    private InventoryTracking inventoryTracking; //none, product, variant
    @JsonInclude
    @JsonProperty("is_visible")
    private boolean isVisible;
    private Availability availability; //available, disabled, preorder
    private String availabilityDescription;
    @JsonSerialize(using = BigCommerceDateSerializer.class)
    private ZonedDateTime preorderReleaseDate;
    private String preorderMessage;
    @JsonInclude
    @JsonProperty("is_preorder_only")
    private boolean isPreorderOnly;
    private CustomUrl customUrl;
    private List<Variant> variants;
    private List<CustomField> customFields;
    private List<ProductImage> images;

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

}