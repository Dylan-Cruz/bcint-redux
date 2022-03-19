package com.dragonslair.bcintredux;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.dragonslair.bcintredux.tasks.ProcessRocaInputTask;
import com.dragonslair.bcintredux.tasks.UpdatePricesTask;
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

    @Autowired
    private UpdatePricesTask updatePricesTask;

    @Autowired
    private ScryfallService scryfallService;
}
