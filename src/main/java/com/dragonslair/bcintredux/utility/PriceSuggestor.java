package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import com.dragonslair.bcintredux.scryfall.enums.Rarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class PriceSuggestor {
    @Value("${dragonslair.mtg.singlesmarkup}")
    private double singlesMarkup;
    @Value("${dragonslair.mtg.playedmarkdown}")
    private double playedMarkdown;
    @Value("#{${dragonslair.mtg.minimumPriceNormal}}")
    private Map<String, Double> minPriceMapNormal;
    @Value("#{${dragonslair.mtg.minimumPriceFoil}}")
    private Map<String, Double> minPriceMapFoil;
    @Value("#{${dragonslair.mtg.minimumPriceEtched}}")
    private Map<String, Double> minPriceMapEtched;
    @Value("#{${dragonslair.mtg.conditionmarkdowns}}")
    private Map<String, Integer> markdownRates;

    public double getPriceSuggestion(Finish finishInHand, Rarity rarity, Condition condition, double price) {
        double ourPrice;

        // apply our markup/markdown
        ourPrice = condition == Condition.NM ? price * (1 + singlesMarkup) : price * (1 + singlesMarkup - playedMarkdown);

        // apply our minimum and round
        ourPrice = applyMinimum(finishInHand, rarity, condition, ourPrice);

        return ourPrice;
    }


    /**
     * This method applies a 2 decimal point rounding convention to a passed double.
     */
    private double priceRounding(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private double applyMinimum(Finish finishInHand, Rarity rarity, Condition condition, double price) {
        String cardRarityName = rarity.getName();
        Map<String, Double> mapToUse = switch(finishInHand) {
            case nonfoil -> minPriceMapNormal;
            case foil -> minPriceMapFoil;
            default -> minPriceMapEtched;
        };
        double minimumPrice = mapToUse.get(cardRarityName);

        if (condition == Condition.NM) {
            return priceRounding(price < minimumPrice ? minimumPrice : price);
        } else {
            if (rarity == Rarity.RARE || rarity == Rarity.SPECIAL || rarity == Rarity.MYTHIC) {
                return priceRounding(price < minimumPrice ? minimumPrice * (1 - playedMarkdown) : price);
            } else {
                return priceRounding(price < minimumPrice ? minimumPrice : price);
            }
        }
    }
}
