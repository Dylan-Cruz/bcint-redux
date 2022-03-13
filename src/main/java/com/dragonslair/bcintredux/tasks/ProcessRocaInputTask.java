package com.dragonslair.bcintredux.tasks;

import com.dragonslair.bcintredux.enums.Condition;
import com.dragonslair.bcintredux.model.QuantityUpdate;
import com.dragonslair.bcintredux.services.MtgAutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
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

    @Value("${aws.bucket.name}")
    private String bucketName;

    // constants
    private final static String regexToSplit = "(?!\\B\"[^\"]*),(?![^\"]*\"\\B)";
    private final static String CONDITION_KEY = "Condition";
    private final static String QUANTITY_KEY = "Add to Quantity";
    private final static String SCRYFALL_ID_KEY = "Scryfall Id";
    private final static String FOIL_KEY = "Foil";


    @Scheduled(cron="${dragonslair.mtg.processroca.schedule}")
    public void runTask() {
        log.info("Starting process roca input task.");

        for (S3Object s3o : getFilesToProcess()) {
            String key = s3o.key();
            log.info("Processing file {}...", key);

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
                    List<QuantityUpdate> jobs = reader.lines()
                            .map(l -> Arrays.asList(l.split(regexToSplit)))
                            .map(fields -> automationService.addQuantityToVariant(
                                    fields.get(scryfallIndex),
                                    parseQuantityFromLine(fields.get(quantityIndex)),
                                    parseConditionFromLine(fields.get(conditionIndex)),
                                    Boolean.parseBoolean(fields.get(foilIndex))))
                            .toList();
                    log.info("Processed {} quantity updates", jobs.size());

                    // write the jobs out to a results file with the same name
                    String filename = "output/"+key.substring(key.lastIndexOf("/")+1, key.lastIndexOf(".csv"))+"_OUTPUT.csv";
                    log.info("Writing output file {}", filename);
                    bcintS3Client.putObject(r -> r.bucket(bucketName).key(filename), RequestBody.fromString(
                        writeJobHeaderDelimitedLine() +
                            jobs.stream().map(it -> writeJobToDelimitedLine(it))
                                    .collect(Collectors.joining("\n"))
                    ));

                    // move the file to the processed 'dir'
                    log.info("Moving input file to processed/");
                    bcintS3Client.copyObject(r -> r.sourceBucket(bucketName)
                            .sourceKey(key)
                            .destinationBucket(bucketName)
                            .destinationKey("processed/"+key.substring(key.lastIndexOf("/")+1))
                    );
                    bcintS3Client.deleteObject(r -> r.bucket(bucketName).key(key));

                    log.info("Processing complete for file {}", key);
                } catch (Exception e) {
                    log.error("An error occurred processing file for key {}.", key, e);
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
            ).contents().stream()
                    .filter(o -> o.key().matches("toProcess/.+"))
                    .toList();
        } catch(RuntimeException e) {
            throw new RuntimeException("An error occurred finding eligible files to process. Process will be aborted.", e);
        }
    }

    private boolean validateFileExtension(String key) {
        boolean valid = key.toLowerCase().endsWith(".csv");
        if (!valid){
            log.error("File with key {} is not a csv file. It will not process. Verify and replace the file to try again. Ensure the old one is deleted.", key);
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

    // utility method to parse condition from the line
    private Condition parseConditionFromLine(String s) {
        if (s == null || s.isEmpty() || s.isEmpty()) {
            return null;
        } else {
            return s.equalsIgnoreCase("Near Mint") ? Condition.NM : Condition.PL;
        }
    }

    private String writeJobToDelimitedLine(QuantityUpdate aqJob) {
        return new StringBuilder()
                .append(aqJob.getCardName()).append(",")
                .append(aqJob.getSet()).append(",")
                .append(aqJob.getCollectorNumber()).append(",")
                .append(aqJob.getScryfallId()).append(",")
                .append(aqJob.getCondition().getLongForm()).append(",")
                .append(aqJob.isFoilInHand()).append(",")
                .append(aqJob.getTargetSku()).append(",")
                .append(aqJob.getStatus().toString()).append(",")
                .append(aqJob.getMessage()).append(",")
                .append(aqJob.getStartingQuantity()).append(",")
                .append(aqJob.getQuantityToAdd()).append(",")
                .append(aqJob.getEndingQuantity()).append(",")
                .append(aqJob.getStartingPrice()).append(",")
                .append(aqJob.getEndingPrice())
                .toString();
    }

    private String writeJobHeaderDelimitedLine() {
        return new StringBuilder()
                .append("Card Name,")
                .append("Set Name,")
                .append("Number,")
                .append("Scryfall Id,")
                .append("Condition,")
                .append("Foil In Hand,")
                .append("Target SKU,")
                .append("Status,")
                .append("Message,")
                .append("Starting Quantity,")
                .append("Quantity To Add,")
                .append("Ending Quantity,")
                .append("Starting Price,")
                .append("Ending Price")
                .append("\n")
                .toString();
    }
}
