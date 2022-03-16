package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;

public class SkuBuilderTests {
    private ScryfallCard card = new ScryfallCard().setCollectorNumber("123")
                                                    .setSet("tst");
/*
    @Test
    public void rootSkuIsCorrectWhenFoil() {
        String rookSku = SkuBuilder.getRootSku(card, true);
        assertEquals(rookSku, "MTGSTST123F");
    }

    @Test
    public void rootSkuIsCorrectWhenNotFoil() {
        String rookSku = SkuBuilder.getRootSku(card, false);
        assertEquals(rookSku, "MTGSTST123R");
    }

    @Test
    public void variantSkuIsCorrectWhenFoil() {
        String rookSku = SkuBuilder.getVariantSku(card, true, Condition.NM);
        assertEquals(rookSku, "MTGSTST123FNM");
    }

    @Test
    public void variantSkuIsCorrectWhenNotFoil() {
        String rookSku = SkuBuilder.getVariantSku(card, false, Condition.PL);
        assertEquals(rookSku, "MTGSTST123RPL");
    }

 */
}
