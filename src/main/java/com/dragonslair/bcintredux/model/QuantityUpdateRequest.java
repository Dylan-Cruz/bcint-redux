package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class QuantityUpdateRequest {
    private String scryfallId;
    private int quantity;
    private Condition condition;
    private Finish finish;
}
