package com.e_commerce.eCommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class R2Config {

    private final R2Properties properties;

    @Bean
    public S3Client s3Client() {

        return S3Client.builder()

                .endpointOverride(
                        URI.create(properties.getEndpoint())
                )

                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                )


                .region(Region.US_EAST_1)

                .forcePathStyle(true)

                .build();
    }

}