package org.abhijitsarkar.springintegration.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.aws.support.S3RemoteFileTemplate;
import org.springframework.integration.aws.support.filters.S3PersistentAcceptOnceFileListFilter;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.remote.AbstractFileInfo;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.messaging.PollableChannel;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static org.abhijitsarkar.springintegration.Application.INPUT_CHANNEL;

/**
 * @author Abhijit Sarkar
 */
@MessageEndpoint
@Profile("inbound-s3")
@EnableConfigurationProperties(S3Properties.class)
public class InboundS3Configuration {
    @Bean
    public S3RemoteFileTemplate template(AmazonS3 amazonS3) {
        return new S3RemoteFileTemplate(amazonS3);
    }

    @Bean
    @InboundChannelAdapter(INPUT_CHANNEL)
    public S3StreamingMessageSource s3InboundStreamingMessageSource(S3RemoteFileTemplate template, S3Properties s3Properties) {
        Comparator<AbstractFileInfo<S3ObjectSummary>> comparator =
                comparing((AbstractFileInfo<S3ObjectSummary> f) -> f.getModified())
                        .reversed();

        S3StreamingMessageSource messageSource = new S3StreamingMessageSource(template, comparator);
        messageSource.setRemoteDirectory(s3Properties.getBucket());
        messageSource.setMaxFetchSize(s3Properties.getMaxFetchSize());

        CompositeFileListFilter<S3ObjectSummary> compositeFileListFilter = new CompositeFileListFilter<>();
        compositeFileListFilter.addFilter(new S3LastModifiedFileListFilter(s3Properties.getLastModifiedWithinSeconds()));
        compositeFileListFilter.addFilter(new S3PersistentAcceptOnceFileListFilter(new SimpleMetadataStore(),
                "streaming"));
        messageSource.setFilter(compositeFileListFilter);

        return messageSource;
    }

    @Bean(INPUT_CHANNEL)
    public PollableChannel inputChannel(S3Properties s3Properties) {
        return new QueueChannel(s3Properties.getMaxFetchSize());
    }

    @Bean
    public AmazonS3 amazonS3(S3Properties s3Properties) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(s3Properties.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        return amazonS3;
    }
}
