package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddQuantityJob {
    private String scryfallId;
    private String targetSku;
    private Condition condition;
    private boolean foilInHand;
    private int quantityToAdd;
    private int startingQuantity;
    private int endingQuantity;
    private int startingPrice;
    private int endingPrice;
    private OperationStatus status;
    private String message;


    public AddQuantityJob(String scryfallId, int quantityToAdd, Condition condition, boolean foilInHand) {
        this.scryfallId = scryfallId;
        this.quantityToAdd = quantityToAdd;
        this.condition = condition;
        this.foilInHand = foilInHand;
    }
}
