package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdatePricesTask {
    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private BigCommerceService bcService;

    public void runTask() {
        try {
            bcService.getInStockProductsForCategoryId(Categories.MAGICSINGLES.getID())
                    .stream().flatMap(p -> p.getVariants()
                            .stream()
                            .filter(v -> v.getInventoryLevel() > 0))
                    .map(automationService::updatePriceOfVariant);
        } catch (RuntimeException re) {
            log.error("Unexpected error running update prices task", re);
        }
    }
}
