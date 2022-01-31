package com.fih.cr.sjm.tico.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    private final String accessKey;
    private final String secretKey;
    private final Regions s3Region;

    public AWSConfig(
            @Value("${amazon.aws.accesskey}") final String accessKey,
            @Value("${amazon.aws.secretkey}") final String secretKey,
            @Value("${amazon.aws.s3.region}") final String s3Region
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.s3Region = Regions.fromName(s3Region);
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                this.accessKey,
                this.secretKey
        );
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials()))
                .withRegion(this.s3Region)
                .build();
    }
}
