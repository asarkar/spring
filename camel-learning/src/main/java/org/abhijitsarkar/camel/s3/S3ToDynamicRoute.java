package org.abhijitsarkar.camel.s3;

import lombok.RequiredArgsConstructor;
import org.abhijitsarkar.camel.FilenameHeaderMessageProcessor;
import org.abhijitsarkar.camel.OutboundRouter;
import org.abhijitsarkar.camel.http.HttpHeadersMessageProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

import static org.abhijitsarkar.camel.Application.INBOUND_S3_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Component
@RequiredArgsConstructor
@Profile(INBOUND_S3_PROFILE)
public class S3ToDynamicRoute extends RouteBuilder {
    private final S3Properties s3Properties;
    private final HttpHeadersMessageProcessor httpHeadersMessageProcessor;

    @Value("${inbound.timer.period:5000}")
    private long inboundTimerDelay;

    @Value("${inbound.cache.size:200}")
    private int inboundCacheSize;

    private String inboundS3Uri;

    @PostConstruct
    void init() {
        inboundS3Uri = UriComponentsBuilder.fromUriString(String.format("aws-s3://%s", s3Properties.getBucket()))
                .queryParam("amazonS3Client", "#amazonS3")
                .queryParam("deleteAfterRead", false)
                .queryParam("prefix", s3Properties.getPrefix())
                .queryParam("includeBody", true)
                .queryParam("maxMessagesPerPoll", s3Properties.getMaxFetchSize())
                .queryParam("delay", inboundTimerDelay)
                .build()
                .toUriString();
    }

    @Override
    public void configure() throws Exception {
        S3LastModifiedFilter lastModifiedFilter =
                new S3LastModifiedFilter(s3Properties.getLastModifiedWithinSeconds(), s3Properties.getPrefix());

        getContext().setTracing(true);

        interceptSendToEndpoint("http4://*")
                .process(httpHeadersMessageProcessor);

        from(inboundS3Uri)
                .filter().method(lastModifiedFilter)
                .idempotentConsumer(header(S3Constants.KEY),
                        MemoryIdempotentRepository.memoryIdempotentRepository(inboundCacheSize))
                // Don't use this for big files as it must read the content into memory
                // to be able to convert to another format.
//                .convertBodyTo(byte[].class)
                .process(new FilenameHeaderMessageProcessor())
                .log(LoggingLevel.INFO, log.getName(), "Processing [${header." + Exchange.FILE_NAME + "}]")
                .dynamicRouter().method(OutboundRouter.class);
    }
}
