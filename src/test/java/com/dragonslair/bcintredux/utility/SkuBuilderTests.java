package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkuBuilderTests {
    private ScryfallCard card = new ScryfallCard().setCollectorNumber("123")
                                                    .setSet("tst");

    @Test
    public void rootSkuIsCorrectWhenFoil() {
        String rookSku = SkuBuilder.getRootSku(card, Finish.foil);
        assertEquals("MTGSTST123F", rookSku);
    }

    @Test
    public void rootSkuIsCorrectWhenNotFoil() {
        String rookSku = SkuBuilder.getRootSku(card, Finish.nonfoil);
        assertEquals("MTGSTST123R", rookSku);
    }

    @Test
    public void rootSkuIsCorrectWhenEtched() {
        String rookSku = SkuBuilder.getRootSku(card, Finish.etched);
        assertEquals("MTGSTST123E", rookSku);
    }

    @Test
    public void rootSkuIsCorrectWhenGlossy() {
        String rookSku = SkuBuilder.getRootSku(card, Finish.glossy);
        assertEquals("MTGSTST123G", rookSku);
    }

    @Test
    public void variantSkuIsCorrectWhenFoilAndNM() {
        String rookSku = SkuBuilder.getVariantSku(card, Finish.foil, Condition.NM);
        assertEquals("MTGSTST123FNM", rookSku);
    }

    @Test
    public void variantSkuIsCorrectWhenNotFoilAndPL() {
        String rookSku = SkuBuilder.getVariantSku(card, Finish.nonfoil, Condition.PL);
        assertEquals("MTGSTST123RPL", rookSku);
    }

    @Test
    public void variantSkuIsCorrectWhenEtchedAndNM() {
        String rookSku = SkuBuilder.getVariantSku(card, Finish.etched, Condition.NM);
        assertEquals("MTGSTST123ENM", rookSku);
    }

    @Test
    public void variantSkuIsCorrectWhenGlossyAndPL() {
        String rookSku = SkuBuilder.getVariantSku(card, Finish.glossy, Condition.PL);
        assertEquals("MTGSTST123GPL", rookSku);
    }

}
