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

import java.util.Collections;
import java.util.List;

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
            // create the category if it doesn't exist
            Category category = bcService.getCategoryByName(set.getName());
            if (category == null) {
                log.debug("Creating category for set {}", set.getName());
                category = bcService.createCategory(buildCategoryFromSet(set));
            }

            log.info("Listing products for set {}", set.getName());

            // get all the existing products for this set
            List<Product> existing = bcService.get

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
