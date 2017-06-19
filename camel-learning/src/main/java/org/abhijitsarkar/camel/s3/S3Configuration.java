package org.abhijitsarkar.camel.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.abhijitsarkar.camel.Application.INBOUND_S3_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@Profile(INBOUND_S3_PROFILE)
public class S3Configuration {
    @Bean
    public AmazonS3 amazonS3(S3Properties s3Properties) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxConnections(s3Properties.getMaxConnections())
                .withConnectionTimeout(s3Properties.getConnectTimeoutMillis())
                .withSocketTimeout(s3Properties.getReadTimeoutMillis())
                .withConnectionMaxIdleMillis(s3Properties.getMaxIdleMillis());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfiguration)
                .withRegion(s3Properties.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        return amazonS3;
    }
}
