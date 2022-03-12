package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdatePricesTask {
    @Autowired
    private MtgAutomationService automationService;

    public void runTask() {
        // get all products in stock

        // flat map and filter down to in stock variants

        // update each variant
    }
}
