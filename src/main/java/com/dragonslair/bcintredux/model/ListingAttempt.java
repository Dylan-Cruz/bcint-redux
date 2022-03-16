package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.OperationStatus;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ListingAttempt {
    private String sku;
    private ScryfallCard card;
    private Finish finish;
    private OperationStatus status = OperationStatus.NOT_STARTED;
    private String message = "";

    public ListingAttempt(String sku, ScryfallCard card, Finish finish) {
        this.sku = sku;
        this.card = card;
        this.finish = finish;
    }
}
