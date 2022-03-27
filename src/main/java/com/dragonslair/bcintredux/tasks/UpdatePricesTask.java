package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.enums.Categories;
import com.dragonslair.bcintredux.model.PriceUpdate;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpdatePricesTask {
    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private BigCommerceService bcService;

    @Value("${dragonslair.mtg.updateprices.enabled}")
    private boolean enabled;

    @Scheduled(cron="${dragonslair.mtg.updateprices.schedule}")
    public void runTask() {
        if (enabled) {
            try {
                log.info("Starting update prices task...");

                List<PriceUpdate> pus = bcService.getSubCategoriesForParent(Categories.MAGICSINGLES.getID()).stream()
                        .map(c -> bcService.getVisibleInStockProductsForCategory(c.getId())
                                .stream()
                                .flatMap(p -> p.getVariants()
                                        .stream()
                                        .filter(v -> v.getInventoryLevel() > 0))
                                .map(automationService::updatePriceOfVariant)
                                .filter(pu -> pu!=null)
                                .collect(Collectors.toList())
                        ).flatMap(l -> l.stream()).collect(Collectors.toList());

                log.info("Update prices task complete. Performed {} updates.", pus.size());
            } catch (Exception re) {
                log.error("Unexpected error running update prices task", re);
            }
        } else {
            log.info("Update prices tasks is disabled.");
        }
    }
}
