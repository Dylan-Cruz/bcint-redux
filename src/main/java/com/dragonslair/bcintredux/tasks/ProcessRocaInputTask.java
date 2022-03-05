package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.rollbar.notifier.Rollbar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class ProcessRocaInputTask {

    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private S3Client bcintS3Client;

    @Autowired
    private Rollbar rollbar;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public void runTask() {
        for (S3Object s3o : getFilesToProcess()) {
            String key = s3o.key();

            // if the file has a valid extension
            if (validateFileExtension(key)) {
                try {
                    // get the file as a buffered stream



                } catch (RuntimeException e) {
                    rollbar.error(e, "An error occurred processing file for key " + key + ". We'll try again next time.");
                }
            }
        }
    }

    /**
     * Returns a list of files in toProcess
     * @return List of S3Objects with prefix toProcess/
     */
    private List<S3Object> getFilesToProcess() {
        try {
            // get all files that haven't changed in size since the last time we checked
            return bcintS3Client.listObjects(r ->
                    r.bucket(bucketName)
                    .prefix("toProcess/")
            ).contents();
        } catch(RuntimeException e) {
            throw new RuntimeException("An error occurred finding eligible files to process. Process will be aborted.", e);
        }
    }

    private boolean validateFileExtension(String key) {
        boolean valid = key.toLowerCase().endsWith(".csv");
        if (!valid){
            rollbar.error("File with key " + key + "is not a csv file. It will not process. Verify and replace the file to try again. Ensure the old one is deleted.");
        }
        return valid;
    }

    private InputStream getS3ObjectAsInputStream(String key) {
        return bcintS3Client.getObject(r -> r.bucket(bucketName).key(key), ResponseTransformer.toInputStream());
    }

    private boolean validateFileContents() {
        // file is not empty
        // fields are comma delimited
        // column Scryfall Id is present
        // column Add to Quantity is present
        return false;
    }

    private void processFile() {
        // get the file

        // create an empty list of AddQuantityJobs

        // for each row
            // call mtg automation service to add the quantity

        // write out the results to a file

        // post the file to AWS
    }
}
