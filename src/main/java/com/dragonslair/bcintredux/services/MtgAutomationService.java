package com.dragonslair.bcintredux.services;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.model.AddQuantityJob;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.utility.PriceSuggestor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MtgAutomationService {
    @Autowired
    private BigCommerceService bcService;

    @Autowired
    private ScryfallService sfService;

    @Autowired
    private PriceSuggestor priceSuggestor;

    /**
     * Updates a given variant matching the scryfall card, condition, and foiling
     * incrementing quantity and updating the price.
     * @param scryfallId
     * @param quantity
     * @param condition
     * @param foilInHand
     * @return aqJob
     */
    public AddQuantityJob addQuantity(String scryfallId, int quantity, Condition condition, boolean foilInHand) {
        // make the job to return
        AddQuantityJob aqJob = new AddQuantityJob(scryfallId, quantity, condition, foilInHand);

        try {
            // get the card data from scryfall

            // generate the sku so we can query bigcommerce

            // get the variant by sku

            // update the price

            // update the quantity

            // push the changes to big commerce

            // return the job
        } catch (Exception e) {
            aqJob.setMessage("An unknown error occurred importing quantity for scryfall id: "
                    + scryfallId + "\n"
                    + e.getMessage());
        }

        return aqJob;
    }
}
