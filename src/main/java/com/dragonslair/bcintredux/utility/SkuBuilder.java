package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;

public class SkuBuilder {

    /**
     * Generates the root sku of a root mtg card product. Sku is the
     * concatenation of set code, collector number, and r/f if regular or foil.
     * Additionally, it must be upper case.
     * @param card ScryfallCard object that backs the data for this listing
     * @param finish
     * @return String concatenation of fields that make up the root product sku
     * @author dylan
     */
    public static String getRootSku(ScryfallCard card, Finish finish) {
        return new StringBuilder()
                .append("MTGS")
                .append(card.getSet())
                .append(card.getCollectorNumber())
                .append(finish.getSkuCode())
                .toString()
                .toUpperCase();
    }

    /**
     * Generates the variant sku of the mtg sub product. Sku is concatenation of
     * setCode, collectorNumber, foil, condition. Additionally, it must be upper case.
     * @param card
     * @param finish
     * @param condition
     * @return Concatenation of fields that make up the variant sku of the sub product
     * @author dylan
     */
    public static String getVariantSku(ScryfallCard card, Finish finish, Condition condition) {
        return new StringBuilder()
                .append(getRootSku(card, finish))
                .append(condition.toString())
                .toString()
                .toUpperCase();
    }
}
