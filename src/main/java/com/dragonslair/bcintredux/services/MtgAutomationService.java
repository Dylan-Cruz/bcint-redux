package com.dragonslair.bcintredux.services;

import com.dragonslair.bcintredux.enums.Condition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MtgAutomationService {

    public void addQuantity(String scryfallId, int Quanity, Condition condition, boolean foilInHand) {
        // get the card data from scryfall

        // generate the sku so we can query bigcommerce

        // get the product by sku

        // update the price of the variant

        // update the quantity

        // push the changes to big commerce
    }
}
