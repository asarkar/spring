package org.abhijitsarkar.camel.s3;

import org.abhijitsarkar.camel.http.ForHttpMessageProcessor;
import org.abhijitsarkar.camel.http.HttpProperties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Abhijit Sarkar
 */
@Component
@Profile({"inbound-s3", "outbound-http"})
public class S3ToHttpRoute extends RouteBuilder {
    @Autowired
    private S3Properties s3Properties;

    @Autowired
    private HttpProperties httpProperties;

    @Value("${inbound.timer.period:5000}")
    private long delay;

    @Value("${inbound.cache.size:200}")
    private int inboundCacheSize;

    private String inboundS3Uri;
    private String outboundHttpUri;

    @PostConstruct
    void init() {
        inboundS3Uri = UriComponentsBuilder.fromUriString(String.format("aws-s3://%s", s3Properties.getBucket()))
                .queryParam("amazonS3Client", "#amazonS3")
                .queryParam("deleteAfterRead", false)
                .queryParam("prefix", s3Properties.getPrefix())
                .queryParam("includeBody", true)
                .queryParam("maxMessagesPerPoll", s3Properties.getMaxFetchSize())
                .queryParam("delay", delay)
                .build()
                .toUriString();

        outboundHttpUri = UriComponentsBuilder.fromUriString("http4://notused")
                .queryParam("disableStreamCache", true)
                .queryParam("httpClient.socketTimeout", httpProperties.getReadTimeout())
                .queryParam("httpClient.connectTimeout", httpProperties.getConnectTimeout())
                .build()
                .toUriString();
    }

    @Override
    public void configure() throws Exception {
        S3LastModifiedFilter lastModifiedFilter =
                new S3LastModifiedFilter(s3Properties.getLastModifiedWithinSeconds(), s3Properties.getPrefix());

        from(inboundS3Uri)
                .filter().method(lastModifiedFilter, "accept")
                .idempotentConsumer(header(S3Constants.KEY),
                        MemoryIdempotentRepository.memoryIdempotentRepository(inboundCacheSize))
                .convertBodyTo(byte[].class, UTF_8.name())
                .process(new ForHttpMessageProcessor(httpProperties))
                .to(outboundHttpUri);
    }
}
