package org.abhijitsarkar.camel;

import lombok.RequiredArgsConstructor;
import org.abhijitsarkar.camel.http.HttpHeadersMessageProcessor;
import org.abhijitsarkar.camel.http.HttpProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.util.function.Supplier;

import static org.abhijitsarkar.camel.Application.DEFAULT_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Component
@RequiredArgsConstructor
@Profile(DEFAULT_PROFILE)
public class StreamToMultiRoute extends RouteBuilder {
    private final Supplier<OutputStream> consumer;
    private final HttpProperties httpProperties;
    private final HttpHeadersMessageProcessor httpHeadersMessageProcessor;

    private String outboundHttpUri;
    private String outboundFileUri;

    @PostConstruct
    void init() {
        outboundHttpUri = UriComponentsBuilder.fromUriString("http4://notused")
                .queryParam("disableStreamCache", true)
                .queryParam("httpClient.socketTimeout", httpProperties.getReadTimeoutMillis())
                .queryParam("httpClient.connectTimeout", httpProperties.getConnectTimeoutMillis())
                .build()
                .toUriString();

        outboundFileUri = UriComponentsBuilder.fromUriString("file://build")
                .queryParam("autoCreate", true)
                .queryParam("eagerDeleteTargetFile", true)
                .queryParam("tempFileName", "${date:now:yyyyMMdd-kkmmss}.tmp")
                .build()
                .toUriString();
    }

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);

        from("direct:in")
                .convertBodyTo(byte[].class)
                .process(new FilenameHeaderMessageProcessor())
                .process(httpHeadersMessageProcessor)
                .setHeader("stream", constant(consumer.get()))
                .multicast().parallelProcessing()
                .to(
                        "stream:header",
                        outboundFileUri,
                        outboundHttpUri
                );
    }
}
