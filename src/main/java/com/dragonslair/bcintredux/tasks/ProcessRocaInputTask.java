package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.rollbar.notifier.Rollbar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessRocaInputTask {

    @Autowired
    private MtgAutomationService automationService;

    @Autowired
    private S3Client bcintS3Client;

    @Autowired
    private Rollbar rollbar;

    @Value("${aws.local.dir}")
    private String filepath;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public void runTask() {
        for (String key : getAllFilesNotBeingUploaded()) {
            // if the file doesn't have a valid extension
            if (!validateFileExtension(key)) {
                rollbar.error("File with key " + key + "is not a csv file. It will not process.");
                continue;
            }

            // get the object?
            bcintS3Client.getObject(r -> r.bucket(bucketName), )


        }
    }

    /**
     * Returns a list of object keys that at runtime did not have a change in content
     * length. Also ignores files that were not present at the initial content check
     * @return List of object keys
     */
    private List<String> getAllFilesNotBeingUploaded() {
        try {
            // get all files that haven't changed in size since the last time we checked
            ListObjectsResponse listObjectsResponse = bcintS3Client.listObjects(r -> {
                r.bucket(bucketName)
                        .prefix("toProcess/");
            });

            // get our content lengths twice with a 3-second delay
            Map<String, Long> firstKeysToContentLength = getKeyToContentLengthsMap(listObjectsResponse.contents());
            Thread.sleep(3000);
            Map<String, Long> secondKeysToContentLength = getKeyToContentLengthsMap(listObjectsResponse.contents());

            // get a list of keys who's content length hasn't changed
            return firstKeysToContentLength.keySet().stream()
                    .filter(k -> firstKeysToContentLength.get(k) == secondKeysToContentLength.get(k))
                    .collect(Collectors.toList());
        } catch(Exception e) {
            throw new RuntimeException("An error occurred finding eligible files to process. Process will be aborted.");
        }
    }

    /**
     * Gets the content lenght for all objects and returns a map of key to content length
     * @param s3Objects
     * @return Map S3Object Key to Content length
     */
    private Map<String, Long> getKeyToContentLengthsMap(List<S3Object> s3Objects) {
        Map<String, Long> map = new HashMap<>();

        s3Objects.forEach( s3Object ->
            map.put(s3Object.key(),
                bcintS3Client.headObject(r ->
                        r.bucket(bucketName)
                        .key(s3Object.key())
                    ).contentLength()
                )
        );

        return map;
    }

    private boolean validateFileExtension(String key) {
        // file is .csv file
        return key.toLowerCase().endsWith(".csv");
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
