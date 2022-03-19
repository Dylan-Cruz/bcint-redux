package com.dragonslair.bcintredux.services;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Metafield;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.OperationStatus;
import com.dragonslair.bcintredux.enums.ProductMetafieldKeys;
import com.dragonslair.bcintredux.model.ListingAttempt;
import com.dragonslair.bcintredux.model.ListingAttemptRequest;
import com.dragonslair.bcintredux.model.PriceUpdate;
import com.dragonslair.bcintredux.model.QuantityUpdate;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import com.dragonslair.bcintredux.utility.PriceSuggestor;
import com.dragonslair.bcintredux.utility.ProductUtils;
import com.dragonslair.bcintredux.utility.SkuBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;

@Service
public class MtgAutomationService {
    Logger log = LoggerFactory.getLogger(MtgAutomationService.class);

    @Autowired
    private BigCommerceService bcService;

    @Autowired
    private ScryfallService sfService;

    @Autowired
    private PriceSuggestor priceSuggestor;

    public ListingAttempt listProduct(ListingAttemptRequest listingRequest) {
        ScryfallCard card = listingRequest.getCard();
        Finish finish = listingRequest.getFinish();
        String rootSku = listingRequest.getSku();
        int categoryId = listingRequest.getCategoryId();

        ListingAttempt listing = new ListingAttempt()
                .setCard(card)
                .setFinish(finish)
                .setSku(rootSku);

        try {
            // make the product
            Product p = ProductUtils.buildProduct(card, finish, categoryId);

            // persist it to big commerce
            p = bcService.createProduct(p);
            final int productId = p.getId();

            // add the images
            ProductUtils.makeProductImages(card).forEach(i -> {
                bcService.createProductImage(productId, i);
            });

            // persist a metafield for scryfall id
            bcService.createProductMetafield(productId, new Metafield(ProductMetafieldKeys.SCRYFALLID.name(), card.getId()));

            // update the product as visible
            bcService.updateProduct(productId, new Product().setVisible(true));

        } catch (RuntimeException re) {
            listing.setMessage(re.getMessage())
                    .setStatus(OperationStatus.ERRORED);
            log.error("Error listing product with sku {}", listing.getSku(), re);
        }

        return listing;
    }

    /**
     * Updates the price of a variant
     * @param variant
     * @return
     */
    public PriceUpdate updatePriceOfVariant(Variant variant) {
        // make the price update to return
        PriceUpdate pu = new PriceUpdate()
                .setTargetSku(variant.getSku())
                .setStartingPrice(variant.getPrice());

        try {
            // get the scryfall id from the meta fields
            String scryfallId = bcService.getProductMetafieldMap(variant.getProductId())
                    .get(ProductMetafieldKeys.SCRYFALLID.name());
            pu.setScryfallId(scryfallId);

            // get the card from scryfall
            ScryfallCard card = sfService.getCardById(scryfallId);
            pu.setCardName(card.getName())
                    .setSet(card.getSetName())
                    .setCollectorNumber(card.getCollectorNumber());

            // get the new price
            String variantSku = variant.getSku();
            Finish finishInHand = Finish.fromSkuCode(variantSku.substring(variantSku.length()-3, variantSku.length()-3));
            Condition condition = Condition.valueOf(variantSku.substring(variantSku.length()-2).toUpperCase());
            double oldPrice = variant.getPrice();
            double scryfallPrice = card.getPriceForFinish(finishInHand);
            double newPrice = priceSuggestor.getPriceSuggestion(finishInHand, card.getRarity(), condition, scryfallPrice);

            if (oldPrice != newPrice) {
                // push a patch to bigcommerce
                Variant patchedVariant = bcService.updateVariant(
                        variant.getProductId(),
                        variant.getId(),
                        variantSku,
                        new Variant().setPrice(newPrice));

                pu.setEndingPrice(patchedVariant.getPrice());

                log.info("Price updated on variant with sku: {} from ${} to ${}",
                        variantSku,
                        NumberFormat.getCurrencyInstance().format(variant.getPrice()),
                        NumberFormat.getCurrencyInstance().format(patchedVariant.getPrice()));
            } else {
                pu.setMessage("No update needed.");
            }

            pu.setStatus(OperationStatus.COMPLETED);
        } catch (RuntimeException re) {
            pu.setMessage(re.getMessage())
                    .setStatus(OperationStatus.ERRORED);
            log.error("Error updating price on variant with sku {}", variant.getSku(), re);
        }

        return pu;
    }

    /**
     * Updates a given variant matching the scryfall card, condition, and foiling
     * incrementing quantity and updating the price.
     * @return aqJob
     */
    public QuantityUpdate addQuantityToVariant(String scryfallId, int quantity, Condition condition, Finish finishInHand) {
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
            String variantSku = SkuBuilder.getVariantSku(card, finishInHand, condition);
            qu.setTargetSku(variantSku);

            // get the variant by sku
            Variant variant = bcService.getVariantBySku(variantSku);
            qu.setStartingQuantity(variant.getInventoryLevel())
                    .setStartingPrice(variant.getPrice());

            // update the price and quantity
            int newQuantity = variant.getInventoryLevel() + quantity;
            double newPrice = priceSuggestor.getPriceSuggestion(
                    finishInHand,
                    card.getRarity(),
                    condition,
                    card.getPriceForFinish(finishInHand)
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
            log.error("Error updating quantity on variant with scryfallId: {} sku: {}", scryfallId, qu.getTargetSku(), re);
        }

        return qu;
    }
}