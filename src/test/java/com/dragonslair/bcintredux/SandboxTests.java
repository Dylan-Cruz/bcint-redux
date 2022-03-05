package com.dragonslair.bcintredux;

import com.dragonslair.bcintredux.bigcommerce.BigCommerceService;
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
    public void processRocaTaskTest() {
        inputTask.runTask();
    }

}
