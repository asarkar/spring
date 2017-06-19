package org.abhijitsarkar.camel;

import org.abhijitsarkar.camel.http.ForHttpMessageProcessor;
import org.abhijitsarkar.camel.http.HttpProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Abhijit Sarkar
 */
@Component
@Profile("default")
public class StreamToMultiRoute extends RouteBuilder {
    @Autowired
    private Supplier<OutputStream> consumer;

    @Autowired
    private HttpProperties httpProperties;

    private String outboundHttpUri;
    private String outboundFileUri;

    @PostConstruct
    void init() {
        outboundHttpUri = UriComponentsBuilder.fromUriString("http4://notused")
                .queryParam("disableStreamCache", true)
                .queryParam("httpClient.socketTimeout", httpProperties.getReadTimeout())
                .queryParam("httpClient.connectTimeout", httpProperties.getConnectTimeout())
                .build()
                .toUriString();

        outboundFileUri = UriComponentsBuilder.fromUriString("file://build")
                .queryParam("autoCreate", false)
                .queryParam("eagerDeleteTargetFile", true)
                .queryParam("tempFileName", "${date:now:yyyyMMdd-kkmmss}.tmp")
                .build()
                .toUriString();
    }

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .convertBodyTo(byte[].class, UTF_8.name())
                .process(new ForHttpMessageProcessor(httpProperties))
                .setHeader("stream", constant(consumer.get()))
                .multicast()
                .to(
                        "stream:header",
                        outboundFileUri,
                        outboundHttpUri
                );
    }
}
