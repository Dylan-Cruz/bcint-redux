package com.dragonslair.bcintredux.services;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.BigCommerceServiceException;
import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import com.dragonslair.bcintredux.model.AddQuantityJob;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.scryfall.ScryfallServiceException;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.utility.PriceSuggestor;
import com.dragonslair.bcintredux.utility.SkuBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
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
     * @param aqjob
     * @return aqJob
     */
    public AddQuantityJob processAddQuantity(AddQuantityJob aqJob) {
        // make the job to return
        try {
            // validate

            // break out the fields


            aqJob.setStatus(OperationStatus.IN_PROGRESS);

            // get the card data from scryfall
            ScryfallCard card = sfService.getCardById(scryfallId);

            // generate the sku so we can query bigcommerce
            String variantSku = SkuBuilder.getVariantSku(card, foilInHand, condition);
            aqJob.setTargetSku(variantSku);

            // get the variant by sku
            Variant variant = bcService.getVariantBySku(variantSku);
            aqJob.setStartingQuantity(variant.getInventoryLevel())
                    .setStartingPrice(variant.getPrice());

            // update the price and quantity
            int newQuantity = variant.getInventoryLevel() + quantity;
            double newPrice = priceSuggestor.getPriceSuggestion(
                    foilInHand,
                    card.getRarity(),
                    condition,
                    foilInHand ? card.getPrices().getUsdFoil() : card.getPrices().getUsd()
            );

            Variant patch = new Variant()
                    .setInventoryLevel(newQuantity)
                    .setPrice(newPrice);

            // push the changes to bigcommerce
            variant = bcService.updateVariant(variant.getProductId(),
                    variant.getId(),
                    variantSku,
                    patch);

            aqJob.setEndingQuantity(variant.getInventoryLevel())
                    .setEndingPrice(variant.getPrice())
                    .setStatus(OperationStatus.COMPLETED);

        } catch (ScryfallServiceException sse) {
            aqJob.setMessage(sse.getMessage());
        } catch (BigCommerceServiceException bce) {
            aqJob.setMessage(bce.getMessage());
        } catch (RuntimeException e) {
            aqJob.setMessage("An error occurred adding quantity: "
                + e.getMessage());
        }

        return aqJob;
    }

    /**
     * Validates the job is valid otherwise throws a new exception with the
     * given error message
     * @param aqJob
     */
    private void validateAddQuantityJob(AddQuantityJob aqJob) {
        // sryfall id
            // empty, null
        // quantity > 0
        // condition isn't null
        String scryfallId = aqJob.getScryfallId();
        if (scryfallId.isBlank())
    }
}
