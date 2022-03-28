package com.dragonslair.bcintredux.utility;

import com.dragonslair.bcintredux.bigcommerce.dto.CreateProductImage;
import com.dragonslair.bcintredux.bigcommerce.dto.OptionValue;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.bigcommerce.enums.Availability;
import com.dragonslair.bcintredux.bigcommerce.enums.InventoryTracking;
import com.dragonslair.bcintredux.bigcommerce.enums.Type;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.scryfall.dto.CardFace;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import com.dragonslair.bcintredux.scryfall.enums.ImageStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ListingUtils {

    public static Product buildProduct(ScryfallCard card, Finish finish, int categoryId) {
        // return fields
        Product p = new Product();
        List<Variant> variants = new ArrayList<>();

        // build out our root product
        p.setName(getListingName(card, finish));
        p.setSku(SkuBuilder.getRootSku(card, finish));
        p.setType(Type.physical);
        p.setCategories(Arrays.asList(categoryId));
        p.setAvailability(Availability.available);
        p.setDepth(0.01);
        p.setWidth(0.07);
        p.setHeight(3.0);
        p.setWidth(2.5);
        p.setWeight(0.06);
        p.setPrice(0.00);
        p.setDescription(buildDescription(card, finish));
        p.setVisible(false);
        p.setInventoryTracking(InventoryTracking.variant);
        p.setVariants(variants);

        // set preorder fields if necessary
        boolean isPreorder = card.getReleasedAt().isAfter(LocalDate.now());
        if (isPreorder) {
            p.setAvailability(Availability.preorder);
            p.setPreorderMessage("This product will ship on " + card.getReleasedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            p.setPreorderReleaseDate(card.getReleasedAt().atStartOfDay().atZone(ZoneId.of("America/New_York")));
            p.setPreorderOnly(true);
        }

        // build our variants
        for (Condition condition : Condition.values()) {
            Variant v = new Variant();

            v.setInventoryLevel(0);
            v.setSku(SkuBuilder.getVariantSku(card, finish, condition));
            v.setOptionValues(new ArrayList<>());

            // configure the option value
            OptionValue ov = new OptionValue();
            ov.setOptionDisplayName("Condition");
            ov.setLabel(condition.name());
            ov.setSortOrder(condition.ordinal());
            ov.setDefault(condition.equals(Condition.NM));
            v.getOptionValues().add(ov);

            variants.add(v);
        }

        return p;
    }

    public static List<CreateProductImage> makeProductImages(ScryfallCard card) {
       try {
            // validate the image status of the card. Throw an error if high res scan isn't available.
            ImageStatus imageStatus = card.getImageStatus();
            if (imageStatus != null && imageStatus == ImageStatus.highres_scan ) {

                // image is available, lets build our cpis
                List<CreateProductImage> images = new ArrayList<>();
                if (card.getImageUris() == null && card.getCardFaces() != null) {
                    // double sided card
                    card.getCardFaces().stream().forEach(f -> {
                        CreateProductImage image = new CreateProductImage();
                        image.setImageUrl(f.getImageUris().getLarge());
                        images.add(image);
                    });
                } else {
                    // single sided card
                    CreateProductImage image = new CreateProductImage();
                    image.setImageUrl(card.getImageUris().getLarge());
                    images.add(image);
                }

                return images;

            } else {
                throw new RuntimeException("High res images not available.");
            }
        } catch (RuntimeException re) {
            throw new RuntimeException("Error generating images for card with scryfall id " + card.getId(), re);
        }
    }

    /**
     * Generates the listing name of the big commerce product
     * @param card object that backs the data for this listing
     * @param finish
     * @return Listing name for the big commerce product backed by this card
     * @author dylan
     */
    private static String getListingName(ScryfallCard card, Finish finish) {
        return new StringBuilder().append(card.getName())
                .append("   ")
                .append(card.getSet().toUpperCase())
                .append(" #")
                .append(card.getCollectorNumber())
                .append(card.isOversized() ? " Oversized" : "")
                .append(finish == Finish.foil ? " " + finish.getName() : "")
                .append(finish == Finish.etched ? " " + finish.getName() : "")
                .toString();
    }

    /**
     * Generates the description of the card for the product listing
     * @param card object that backs the data for this listing
     * @return html formatted description of the card
     * @author dylan
     */
    private static String buildDescription(ScryfallCard card, Finish finish) {
        Function<String, String> nullCatch = s -> (s = (s == null ? " " : s));
        Function<String, String> applyParagraphTag = s-> (s = ("<p>" + s + "</p>"));
        Function<String, String> applyBoldTag = s -> (s = ("<strong>" + s + "</strong>"));

        StringBuilder sb = new StringBuilder();

        if (card.getCardFaces() == null) {
            // we have a single sided card
            sb.append(applyParagraphTag.apply(applyBoldTag.apply("Name: ") + nullCatch.apply(card.getName())))
                    .append(applyParagraphTag.apply(applyBoldTag.apply("Mana Cost: ") + nullCatch.apply(card.getManaCost())))
                    .append(applyParagraphTag.apply(applyBoldTag.apply("Type: ") + nullCatch.apply(card.getTypeLine())))
                    .append(applyParagraphTag.apply(applyBoldTag.apply("Oracle Text: ")));

            Arrays.stream(nullCatch.apply(card.getOracleText()).split("\\n")).forEach(line -> {
                sb.append(applyParagraphTag.apply(line));
            });

            if (card.getPower() != null) {
                sb.append(applyParagraphTag.apply(applyBoldTag.apply("Power: ") + card.getPower()));
            }
            if (card.getToughness() != null) {
                sb.append(applyParagraphTag.apply(applyBoldTag.apply("Toughness: ") + card.getToughness()));
            }
            if (card.getLoyalty() != null) {
                sb.append(applyParagraphTag.apply(applyBoldTag.apply("Loaylty: ") + card.getLoyalty()));
            }

        } else {
            // we have a double sided card
            List<CardFace> cardFaces = card.getCardFaces();
            for (int i = 0; i < cardFaces.size(); i++) {
                CardFace cf = cardFaces.get(i);
                int side = i+1;
                sb.append(applyParagraphTag.apply(applyBoldTag.apply("Side " + side)))
                        .append(applyParagraphTag.apply(applyBoldTag.apply("Name: ") + nullCatch.apply(cf.getName())))
                        .append(applyParagraphTag.apply(applyBoldTag.apply("Mana Cost: ") + nullCatch.apply(cf.getManaCost())))
                        .append(applyParagraphTag.apply(applyBoldTag.apply("Type: ") + nullCatch.apply(cf.getTypeLine())))
                        .append(applyParagraphTag.apply(applyBoldTag.apply("Oracle Text: ")));

                Arrays.stream(nullCatch.apply(cf.getOracleText()).split("\\n")).forEach(line -> {
                    sb.append(applyParagraphTag.apply(line));
                });

                if (card.getPower() != null) {
                    sb.append(applyParagraphTag.apply(applyBoldTag.apply("Power: ") + cf.getPower()));
                }
                if (card.getToughness() != null) {
                    sb.append(applyParagraphTag.apply(applyBoldTag.apply("Toughness: ") + cf.getToughness()));
                }
                if (card.getLoyalty() != null) {
                    sb.append(applyParagraphTag.apply(applyBoldTag.apply("Loaylty: ") + cf.getLoyalty()));
                }
            }
        }

        // card id fields
        sb.append(applyParagraphTag.apply(applyBoldTag.apply("Collector Number: ") + nullCatch.apply(card.getCollectorNumber())))
                .append(applyParagraphTag.apply(applyBoldTag.apply("Set Code: ") + nullCatch.apply(card.getSet().toUpperCase())))
                .append(applyParagraphTag.apply(applyBoldTag.apply("Set Name: ") + nullCatch.apply(card.getSetName())));

        // card print fields
        sb.append(applyParagraphTag.apply(applyBoldTag.apply("Rarity: ") + nullCatch.apply(card.getRarity().getName())))
                .append(applyParagraphTag.apply(applyBoldTag.apply("Finish: ") + nullCatch.apply(finish.getName())));
        if (card.isPromo()) {
            sb.append(applyParagraphTag.apply(applyBoldTag.apply("Promo: ") + "True"));
        }
        if (card.isOversized()) {
            sb.append(applyParagraphTag.apply(applyBoldTag.apply("Oversize: ") + "True"));
        }

        return sb.toString();
    }
}
