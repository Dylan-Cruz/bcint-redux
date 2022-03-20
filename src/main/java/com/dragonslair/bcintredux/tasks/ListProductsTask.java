package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Category;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import com.dragonslair.bcintredux.model.ListingAttempt;
import com.dragonslair.bcintredux.model.ListingAttemptRequest;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallCard;
import com.dragonslair.bcintredux.scryfall.dto.ScryfallSet;
import com.dragonslair.bcintredux.scryfall.enums.Finish;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.dragonslair.bcintredux.utility.SkuBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Value("${dragonslair.mtg.listproducts.enabled}")
    private boolean enabled;

    @Scheduled(cron="${dragonslair.mtg.listproducts.schedule}")
    public void runTask() {
        if (enabled) {
            try {
                log.info("Starting list products task...");
                List<ListingAttempt> listingAttempts = sfService.getAllSets()
                        .stream()
                        .filter(s -> (!s.isDigital()
                                // && !"funny".equals(s.getSetType())
                                // && !"token".equals(s.getSetType())
                        ))
                        .map(s -> listSet(s))
                        .flatMap(List::stream)
                        .toList();

                log.info("List products task complete. Listed {} new products", listingAttempts.size());
            } catch (RuntimeException re) {
                log.error("Unexpected error occurred in the list products task. Aborting.", re);
            }
        } else {
            log.info("List products task is disabled.");
        }
    }

    private List<ListingAttempt> listSet(ScryfallSet set) {
        try {
            // get the category and it's products or create it
            Category category = bcService.getCategoryByName(set.getName());
            List<String> existingSkus;
            if (category == null) {
                log.debug("Creating category for set {}", set.getName());
                category = bcService.createCategory(buildCategoryFromSet(set));
                existingSkus = new ArrayList<>();
            } else {
                existingSkus = bcService.getProductsForCategoryId(category.getId()).parallelStream()
                        .map(Product::getSku)
                        .collect(Collectors.toList());
                existingSkus.sort(String::compareTo);

            }

           // make the listings we need
            List<ListingAttemptRequest> listings = new ArrayList<>();
            for (ScryfallCard card : sfService.getCardsForSearchUri(set.getSearchUri())) {
                for (Finish finish : card.getFinishes()) {
                    String rootSku = SkuBuilder.getRootSku(card, finish);
                    if (Collections.binarySearch(existingSkus, rootSku, String::compareTo) < 0) {
                        listings.add(new ListingAttemptRequest(rootSku, card, finish, category.getId()));
                    }
                }
            }

            log.info("Listing {} products for set {}", listings.size(), set.getName());
            return listings.stream().map(mtgService::listProduct).collect(Collectors.toList());

        } catch (RuntimeException re) {
            log.error("Unexpected error occurred listing cards for set {}", set.getName(), re);
            return Collections.emptyList();
        }
    }

    private Category buildCategoryFromSet(ScryfallSet set) {
        return new Category()
                .setName(set.getName())
                .setParentId(Categories.MAGICSINGLES.getID())
                .setVisible(true);
    }
}
