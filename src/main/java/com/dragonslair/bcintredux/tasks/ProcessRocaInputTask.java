package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProcessRocaInputTask {

    @Autowired
    private MtgAutomationService automationService;

    public void runTask() {
        // get all files that haven't changed in size since the last time we checked

        // for each file
            // process the file
    }

    private void processFile() {
        // get the file

        // create an empty list of AddQuantityJobs

        // for each row
            // call mtg automation service to add the quantity

        // write out the results to a file

        // post the file to AWS
    }

    private void getFiles() {

    }

    private void pushOutputFile() {

    }

    private void getDelimitedOutputLine() {

    }
}
