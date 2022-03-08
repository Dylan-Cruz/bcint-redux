package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.model.AddQuantityJob;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import com.rollbar.notifier.Rollbar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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

    // constants
    private final static String regexToSplit = "(?!\\B\"[^\"]*),(?![^\"]*\"\\B)";
    private final static String CONDITION_KEY = "Condition";
    private final static String QUANTITY_KEY = "Add to Quantity";
    private final static String SCRYFALL_ID_KEY = "Scryfall Id";
    private final static String FOIL_KEY = "Foil";

    public void runTask() {
        for (S3Object s3o : getFilesToProcess()) {
            String key = s3o.key();

            // if the file has a valid extension
            if (validateFileExtension(key)) {

                // we got the file open, lets process it
                try (BufferedReader reader = getS3ObjectAsBufferedReader(key)) {

                    // get the first line and run validations
                    String headers = reader.readLine();
                    Map<String, Integer> headersToIndexes = validateHeader(headers);

                    // headers are valid lets get our indexes
                    final int scryfallIndex = headersToIndexes.get(SCRYFALL_ID_KEY);
                    final int quantityIndex = headersToIndexes.get(QUANTITY_KEY);
                    final int conditionIndex = headersToIndexes.get(CONDITION_KEY);
                    final int foilIndex = headersToIndexes.get(FOIL_KEY);

                    // get a list of jobs to process and process them
                    List<AddQuantityJob> jobs = reader.lines()
                            .map(l -> Arrays.asList(l.split(regexToSplit)))
                            .map(fields -> new AddQuantityJob(
                                    fields.get(scryfallIndex),
                                    parseQuantityFromLine(fields.get(quantityIndex)),
                                    parseConditionFromLine(fields.get(conditionIndex)),
                                    Boolean.parseBoolean(fields.get(foilIndex))))
                            .map(job -> automationService.processAddQuantity(job))
                            .toList();


                    // write the jobs out to a results file with the same name

                } catch (Exception e) {
                    rollbar.error(e, "An error occurred processing file for key " + key + ".");
                    continue;
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

    private BufferedReader getS3ObjectAsBufferedReader(String key) {
            return new BufferedReader(
                    new InputStreamReader(
                            bcintS3Client.getObject(r -> r.bucket(bucketName).key(key), ResponseTransformer.toInputStream())
                    )
            );
    }

    /** Runs various checks on the file and header row throwing a runtime exception
     * if an issue is present. Otherwise it returns a map of header field names
     * to their respective indexes in the file
     * @param headers
     * @return Map of column names to their position
     */
    private Map<String, Integer> validateHeader(String headers) {
        // file is not empty
        if (headers == null) {
            throw new RuntimeException("The file is empty.");
        }

        // fields are comma delimited
        List<String> headerFields = Arrays.stream(headers.split(regexToSplit)).toList();
        if (headerFields.size() <= 1) {
            throw new RuntimeException("The header row isn't comma separated.");
        }

        // get a map of header names to index map
        Map<String, Integer> headerToIndexMap = new HashMap<>();
        for (int i = 0; i < headerFields.size(); i++) {
            headerToIndexMap.put(headerFields.get(i), i);
        }

        // column Scryfall Id is present
        if (!headerToIndexMap.containsKey(SCRYFALL_ID_KEY)) {
            throw new RuntimeException("Column " + SCRYFALL_ID_KEY + " is missing.");
        }

        // column Add to Quantity is present
        if (!headerToIndexMap.containsKey(QUANTITY_KEY)) {
            throw new RuntimeException("Column " + QUANTITY_KEY + " is missing.");
        }

        // column Condition is present
        if (!headerToIndexMap.containsKey(CONDITION_KEY)) {
            throw new RuntimeException("Column " + CONDITION_KEY + " is missing.");
        }

        // column Foiling is present
        if (!headerToIndexMap.containsKey(FOIL_KEY)) {
            throw new RuntimeException("Column " + FOIL_KEY + " is missing.");
        }

        return headerToIndexMap;
    }

    // utility method to parse an int from a field in the file
    // returning 0 as default
    private int parseQuantityFromLine(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    // utility method to parse
    private Condition parseConditionFromLine(String s) {
        if (s == null || s.isEmpty() || s.isEmpty()) {
            return null;
        } else {
            return s.equalsIgnoreCase("Near Mint") ? Condition.NM : Condition.PL;
        }
    }
}
