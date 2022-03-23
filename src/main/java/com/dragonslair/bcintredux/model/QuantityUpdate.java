package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class QuantityUpdate extends AutomationOperation {
    private String scryfallId;
    private String targetSku;
    private String cardName;
    private String set;
    private String collectorNumber;
    private Condition condition;
    private Finish finishInHand;
    private int quantityToAdd;
    private int startingQuantity;
    private int endingQuantity;
    private double startingPrice;
    private double endingPrice;
}
