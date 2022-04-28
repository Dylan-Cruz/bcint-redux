package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.scryfall.ScryfallService;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateImageQualityTask {
    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private ScryfallService scryfallService;

    // TODO("setup env variable to disable task")
    private boolean enabled;

    // TODO("setup env variable to disable task")
    public void runTask() {
        // get all categories

        // get all products

        // for each product

        
    }
}

