package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ListingAttemptRequest {
    private String sku;
    private ScryfallCard card;
    private Finish finish;
    private int categoryId;

    public ListingAttemptRequest(String sku, ScryfallCard card, Finish finish, int categoryId) {
        this.sku = sku;
        this.card = card;
        this.finish = finish;
        this.categoryId = categoryId;
    }
}
