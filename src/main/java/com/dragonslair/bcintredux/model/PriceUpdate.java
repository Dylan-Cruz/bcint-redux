package com.dragonslair.bcintredux.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PriceUpdate extends AutomationOperation {
    private String scryfallId;
    private String targetSku;
    private String cardName;
    private String set;
    private String collectorNumber;
    private double startingPrice;
    private double endingPrice;
}
