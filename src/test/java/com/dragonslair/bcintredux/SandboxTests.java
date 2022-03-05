package com.dragonslair.bcintredux;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.bigcommerce.BigCommerceServiceException;
import com.dragonslair.bcintredux.bigcommerce.dto.Variant;
import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.model.AddQuantityJob;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.dragonslair.bcintredux.tasks.ProcessRocaInputTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SandboxTests {

    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private BigCommerceService bigCommerceService;

    @Autowired
    private ProcessRocaInputTask inputTask;

    @Test
    public void seeIfRateLimiterWorks() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("Call " + i);

            try {
                Variant v = bigCommerceService.getVariantBySku("MTGSAKH117RNM");
                System.out.println("Successfully got variant with id: " + v.getId());
            } catch (BigCommerceServiceException exception) {
                System.out.println("ERROR - " + exception.getMessage());
            }
        }
    }

    @Test
    public void testUpdateVariant() {
        Variant v = bigCommerceService.getVariantBySku("MTGSAKH117RNM");
        int oldQuantity = v.getInventoryLevel();
        Variant patch = new Variant().setPrice(9999.9).setInventoryLevel(4);
        v = bigCommerceService.updateVariant(v.getProductId(), v.getId(), "MTGSAKH117RNM", patch);
        System.out.println(v);
    }

    @Test
    public void testAddQuantityMethod() {
        AddQuantityJob aqJob = automationService.addQuantity(
                "02fe76b0-86eb-4f5b-8877-657cb8a37fc3",
                20,
                Condition.NM,
                true
        );
    }

    @Test
    public void processRocaTaskTest() {
        inputTask.runTask();
    }
}
