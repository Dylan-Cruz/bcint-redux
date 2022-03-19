package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.dto.Category;
import com.dragonslair.bcintredux.bigcommerce.dto.Product;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class CleanErroredListingsTask {
    Logger log = LoggerFactory.getLogger(CleanErroredListingsTask.class);

    @Autowired
    private BigCommerceService bcService;

    @Value("${dragonslair.mtg.cleanuplistings.enabled}")
    private boolean enabled;

    @Scheduled(cron="${dragonslair.mtg.cleanuplistings.schedule}")
    public void runTask() {
        if (enabled) {
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

                            } catch (RuntimeException re) {
                                log.error("Error deleting product with sku: {}", p.getSku());
                            }
                        }
                    } catch (RuntimeException re) {
                        log.error("Error removing errored listings for set {}", c.getName(), re);
                    }
                }

            } catch (RuntimeException re) {
                log.error("An unrecoverable error occurred while removing errored listings. Aborting process.", re);
            }
        } else {
            log.info("Cleanup errored listings task disabled.");
        }
    }
}
