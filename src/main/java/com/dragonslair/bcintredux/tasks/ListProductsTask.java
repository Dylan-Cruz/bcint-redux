package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Category;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import com.dragonslair.bcintredux.model.ListingAttempt;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallSet;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ListProductsTask {

    @Autowired
    private ScryfallService sfService;

    @Autowired
    private BigCommerceService bcService;

    @Autowired
    private MtgAutomationService mtgService;

    public void listProducts() {
        try {
            log.info("Starting list products task...");
            List<ListingAttempt> listingAttempts = sfService.getAllSets()
                    .stream()
                    .filter(s -> (!s.isDigital() && "funny".equals(s.getSetType())))
                    .map(s -> listSet(s))
                    .flatMap(List::stream)
                    .toList();
            log.info("List products task complete. Listed {} new products", listingAttempts.size());
        } catch (RuntimeException re) {
            log.error("Unexpected error occurred in the list products task. Aborting.", re);
        }
    }


    private List<ListingAttempt> listSet(ScryfallSet set) {
        try {
            // get the category and it's products or create it
            Category category = bcService.getCategoryByName(set.getName());
            List<Product> existingProducts;
            if (category == null) {
                log.debug("Creating category for set {}", set.getName());
                category = bcService.createCategory(buildCategoryFromSet(set));
                existingProducts = new ArrayList<>();
            } else {
                existingProducts = bcService.getProductsForCategoryId(category.getId())

            }

            // identify the listings we have to make
            List<String> existingSkus = existingProducts.stream()
                    .map(Product::getSku)
                    .collect(Collectors.toList());
            existingSkus.sort(String::compareTo);

            Map<String, ListingAttempt> skusToListingAttempts = sfService.getCardsForSearchUri(set.getSearchUri())
                            .stream()
                                    .collect(Collectors.toMap(c -> ))

            log.info("Listing products for set {}", set.getName());


            // get a list of missing skus

            // for each missing sku, make the product listing


        } catch (RuntimeException re) {
            log.error("Unexpected error occurred listing cards for set {}", set.getName(), re);
        }

        return Collections.emptyList();
    }

    private Category buildCategoryFromSet(ScryfallSet set) {
        return new Category()
                .setName(set.getName())
                .setParentId(Categories.MAGICSINGLES.getID())
                .setVisible(true);
    }
}
