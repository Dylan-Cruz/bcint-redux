package com.dragonslair.bcintredux.services;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import com.dragonslair.bcintredux.model.PriceUpdate;
import com.dragonslair.bcintredux.model.QuantityUpdate;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
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


    public PriceUpdate updatePriceOfVariant(Variant variant) {
        // make the price update to return
        PriceUpdate pu = new PriceUpdate()
                .setTargetSku(variant.getSku())
                .setStartingPrice(variant.getPrice());

        try {
            // get the scryfall id from the meta fields

            // get the card from scryfall

            // get the new price

            // push a patch to bigcommerce

            pu.setStatus(OperationStatus.COMPLETED);
        } catch (RuntimeException re) {
            pu.setMessage(re.getMessage())
                    .setStatus(OperationStatus.ERRORED);
        }

        return pu;
    }

    /**
     * Updates a given variant matching the scryfall card, condition, and foiling
     * incrementing quantity and updating the price.
     * @return aqJob
     */
    public QuantityUpdate addQuantityToVariant(String scryfallId, int quantity, Condition condition, boolean foilInHand) {
        // make the job to return
        QuantityUpdate qu = new QuantityUpdate();

        try {
            // validate
            if (scryfallId == null || scryfallId.isBlank()) {
                throw new RuntimeException("ScryfallId cannot be null or blank");
            }
            if (qu.getQuantityToAdd() <= 0) {
                throw new RuntimeException("Quantity to add cannot be less than 1");
            }
            if (qu.getCondition() == null) {
                throw new RuntimeException("Condition cannot be null.");
            }

            // start processing
            qu.setStatus(OperationStatus.IN_PROGRESS);

            // get the card data from scryfall
            ScryfallCard card = sfService.getCardById(scryfallId);
            qu.setCardName(card.getName());
            qu.setSet(card.getSet());
            qu.setCollectorNumber(card.getCollectorNumber());

            // generate the sku so we can query bigcommerce
            String variantSku = SkuBuilder.getVariantSku(card, foilInHand, condition);
            qu.setTargetSku(variantSku);

            // get the variant by sku
            Variant variant = bcService.getVariantBySku(variantSku);
            qu.setStartingQuantity(variant.getInventoryLevel())
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

            qu.setEndingQuantity(variant.getInventoryLevel())
                    .setEndingPrice(variant.getPrice())
                    .setStatus(OperationStatus.COMPLETED);

        } catch (RuntimeException re) {
            qu.setMessage(re.getMessage())
                    .setStatus(OperationStatus.ERRORED);
        }

        return qu;
    }
}
