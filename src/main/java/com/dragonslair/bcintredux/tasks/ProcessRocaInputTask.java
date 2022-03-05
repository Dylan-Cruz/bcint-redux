package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

@Slf4j
@Component
public class ProcessRocaInputTask {

    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private S3Client bcintS3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public void runTask() {
        // get all files that haven't changed in size since the last time we checked
        ListObjectsResponse listObjectsResponse = bcintS3Client.listObjects(r -> {
            r.bucket(bucketName)
                .prefix("toProcess/");
        });

        listObjectsResponse.contents().forEach(o -> System.out.println(o.key()));




        // for each file
            // process the file
    }

    private void getAllFilesNotBeingUploaded() {

    }

    private void validateFileName() {
        // file is .csv file
        // last char before extension is valid foiling symbol
        // 2nd and 3rd to last chars is valid condition symbol
    }

    private void validateFileContents() {
        // fields are comma delimited
        // column Scryfall Id is present
        // column Add to Quantity is present
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
