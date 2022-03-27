package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=true)
public class ListingAttempt extends AutomationOperation {
    private String sku;
    private ScryfallCard card;
    private Finish finish;
}
