package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class QuantityUpdate {
    private String scryfallId;
    private String targetSku;
    private String cardName;
    private String set;
    private String collectorNumber;
    private Condition condition;
    private boolean foilInHand;
    private int quantityToAdd;
    private int startingQuantity;
    private int endingQuantity;
    private double startingPrice;
    private double endingPrice;
    private OperationStatus status = OperationStatus.NOT_STARTED;
    private String message = "";
}
