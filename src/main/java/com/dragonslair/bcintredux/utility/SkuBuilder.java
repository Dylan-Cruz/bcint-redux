package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkuBuilder {

    /**
     * Generates the root sku of a root mtg card product. Sku is the
     * concatenation of set code, collector number, and r/f if regular or foil.
     * Additionally, it must be upper case.
     * @param card ScryfallCard object that backs the data for this listing
     * @return String concatenation of fields that make up the root product sku
     * @author dylan
     */
    public static String getRootSku(ScryfallCard card, boolean foil) {
        StringBuilder sb = new StringBuilder()
                .append("MTGS")
                .append(card.getSet())
                .append(card.getCollectorNumber())
                .append(foil?"F":"R");

        log.debug("Generated sku: " + sb.toString());
        return sb.toString().toUpperCase();
    }

    /**
     * Generates the variant sku of the mtg sub product. Sku is concatenation of
     * setCode, collectorNumber, foil, condition. Additionally, it must be upper case.
     * @param card
     * @param foil
     * @param condition
     * @return Concatenation of fields that make up the variant sku of the sub product
     * @author dylan
     */
    public static String getVariantSku(ScryfallCard card, boolean foil, Condition condition) {
        StringBuilder sb = new StringBuilder()
                .append(getRootSku(card, foil))
                .append(condition.toString());
        log.debug("Generated variant sku: " + sb.toString());
        return sb.toString().toUpperCase();
    }
}
