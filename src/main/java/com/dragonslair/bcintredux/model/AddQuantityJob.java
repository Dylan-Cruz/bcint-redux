package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class AddQuantityJob {
    private String scryfallId;
    private String targetSku;
    private Condition condition;
    private boolean foilInHand;
    private int quantityToAdd;
    private int startingQuantity;
    private int endingQuantity;
    private double startingPrice;
    private double endingPrice;
    private OperationStatus status = OperationStatus.NOT_STARTED;
    private String message;


    public AddQuantityJob(String scryfallId, int quantityToAdd, Condition condition, boolean foilInHand) {
        this.scryfallId = scryfallId;
        this.quantityToAdd = quantityToAdd;
        this.condition = condition;
        this.foilInHand = foilInHand;
    }
}
