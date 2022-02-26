package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.enums.Rarity;
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

    public double getPriceSuggestion(boolean foilInHand, Rarity rarity, Condition condition, double price) {
        double ourPrice;

        // apply our markup/markdown
        ourPrice = condition == Condition.NM ? price * (1 + singlesMarkup) : price * (1 + singlesMarkup - playedMarkdown);

        // apply our minimum and round
        ourPrice = applyMinimum(foilInHand, rarity, condition, ourPrice);

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


    private double applyMinimum(boolean foilInHand, Rarity rarity, Condition condition, double price) {
        String cardRarityName = rarity.getName();
        Map<String, Double> mapToUse = (foilInHand ? minPriceMapFoil : minPriceMapNormal);
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
