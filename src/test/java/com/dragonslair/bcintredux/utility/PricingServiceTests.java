package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.enums.Rarity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PricingServiceTests {
    @Autowired
    private PriceSuggestor pricingService;

    @Value("${dragonslair.mtg.singlesmarkup}")
    private double singlesMarkup;
    @Value("${dragonslair.mtg.playedmarkdown}")
    private double playedMarkdown;
    @Value("#{${dragonslair.mtg.minimumPriceNormal}}")
    private Map<String, Double> minPriceMapNormal;
    @Value("#{${dragonslair.mtg.minimumPriceFoil}}")
    private Map<String, Double> minPriceMapFoil;

    @Test
    public void nmMythicRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.MYTHIC.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.MYTHIC, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmMythicRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.MYTHIC.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.MYTHIC, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmMythicFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.MYTHIC.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.MYTHIC, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmMythicFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.MYTHIC.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.MYTHIC, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmSpecialRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.SPECIAL.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.SPECIAL, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmSpecialRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.SPECIAL.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.SPECIAL, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmSpecialFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.SPECIAL.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.SPECIAL, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmSpecialFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.SPECIAL.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.SPECIAL, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmRareRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.RARE.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.RARE, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmRareRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.RARE.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.RARE, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmRareFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.RARE.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.RARE, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmRareFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.RARE.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.RARE, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmUncommonRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.UNCOMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.UNCOMMON, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmUncommonRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.UNCOMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.UNCOMMON, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmUncommonFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.UNCOMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.UNCOMMON, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmUncommonFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.UNCOMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.UNCOMMON, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }


    @Test
    public void nmCommonRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.COMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.COMMON, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmCommonRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.COMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.COMMON, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmCommonFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.COMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.COMMON, Condition.NM, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void nmCommonFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.COMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.COMMON, Condition.NM, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plMythicRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.MYTHIC.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.MYTHIC, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plMythicRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.MYTHIC.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.MYTHIC, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plMythicFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.MYTHIC.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.MYTHIC, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plMythicFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.MYTHIC.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.MYTHIC, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plSpecialRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.SPECIAL.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.SPECIAL, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plSpecialRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.SPECIAL.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.SPECIAL, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plSpecialFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.SPECIAL.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.SPECIAL, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plSpecialFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.SPECIAL.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.SPECIAL, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plRareRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.RARE.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.RARE, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plRareRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.RARE.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.RARE, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plRareFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.RARE.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.RARE, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plRareFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.RARE.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum * (1 - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.RARE, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plUncommonRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.UNCOMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.UNCOMMON, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plUncommonRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.UNCOMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.UNCOMMON, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plUncommonFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.UNCOMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.UNCOMMON, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plUncommonFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.UNCOMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.UNCOMMON, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plCommonRegularOver() {
        double minimum = minPriceMapNormal.get(Rarity.COMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.COMMON, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plCommonRegularUnder() {
        double minimum = minPriceMapNormal.get(Rarity.COMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(false, Rarity.COMMON, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plCommonFoilOver() {
        double minimum = minPriceMapFoil.get(Rarity.COMMON.getName());
        double overPrice = minimum * 2;
        double expectedPrice = overPrice * (1 + singlesMarkup - playedMarkdown);
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.COMMON, Condition.PL, overPrice);
        assertEquals(expectedPrice, suggestedPrice, .01, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }

    @Test
    public void plCommonFoilUnder() {
        double minimum = minPriceMapFoil.get(Rarity.COMMON.getName());
        double underPrice = minimum / 2;
        double expectedPrice = minimum;
        double suggestedPrice = pricingService.getPriceSuggestion(true, Rarity.COMMON, Condition.PL, underPrice);
        assertEquals(expectedPrice, suggestedPrice, "Expected Price: " + expectedPrice + " does not equal Suggested Price: " + suggestedPrice);
    }
}