package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Category;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
public class CleanErroredListingsTask {

    @Autowired
    private BigCommerceService bcService;

    @Value("${dragonslair.mtg.cleanuplistings.enabled}")
    private boolean enabled;

    @Scheduled(cron="${dragonslair.mtg.cleanuplistings.schedule}")
    public void runTask() {
        if (enabled) {
            int cardsRemoved = 0;

            log.info("Starting process to remove errored listings");

            try {

                for (Category c : bcService.getSubCategoriesForParent(Categories.MAGICSINGLES.getID())) {
                    try {
                        MultiValueMap<String, String> args = new LinkedMultiValueMap<>();
                        args.add("is_visible", "false");
                        args.add("categories:in", Integer.toString(c.getId()));
                        args.add("inventory_level", "0");

                        for (Product p : bcService.searchProducts(args)) {
                            try {
                                bcService.deleteProduct(p.getId());
                                cardsRemoved++;
                            } catch (Exception re) {
                                log.error("Error deleting product with sku: {}", p.getSku(), re);
                            }
                        }
                    } catch (Exception re) {
                        log.error("Error removing errored listings for set {}", c.getName(), re);
                    }
                }

            } catch (Exception re) {
                log.error("An unrecoverable error occurred while removing errored listings. Aborting process.", re);
            }

            log.info("Cleanup errored listings task complete. Removed {} cards.", cardsRemoved);
        } else {
            log.info("Cleanup errored listings task disabled.");
        }
    }
}
