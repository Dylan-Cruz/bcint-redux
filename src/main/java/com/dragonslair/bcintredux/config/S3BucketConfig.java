package com.dragonslair.bcintredux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3BucketConfig {

    @Bean
    public S3Client s3Client(
            @Value("${aws.access.key.id}") String accessKeyId,
            @Value("${aws.secret.key.id}") String secretKeyId
    ) {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                accessKeyId,
                                secretKeyId)
                ))
                .region(Region.US_EAST_1)
                .build();
    }
}
