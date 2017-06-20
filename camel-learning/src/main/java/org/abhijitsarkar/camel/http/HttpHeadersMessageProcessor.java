package org.abhijitsarkar.camel.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.camel.Exchange.CONTENT_TYPE;
import static org.apache.camel.Exchange.FILE_NAME;
import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.Exchange.HTTP_URI;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HttpHeadersMessageProcessor implements org.apache.camel.Processor {
    private final HttpProperties httpProperties;

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Map<String, Object> inHeaders = in.getHeaders();
        log.debug("In headers: {}.", inHeaders);

        Message out = exchange.getOut();
        Map<String, Object> outHeaders = out.getHeaders();

        // Without this, out body is null
        out.setBody(in.getBody());

        Object filename = inHeaders.get(FILE_NAME);

        Assert.notNull(filename, String.format("%s header is required.", FILE_NAME));

        out.setHeader(FILE_NAME, inHeaders.get(FILE_NAME));

        outHeaders.putIfAbsent(CONTENT_TYPE, Optional.ofNullable(inHeaders.get(CONTENT_TYPE))
                .orElse(httpProperties.getContentTypeHeader()));

        outHeaders.putIfAbsent(HTTP_METHOD, Optional.ofNullable(inHeaders.get(HTTP_METHOD))
                .orElse(httpProperties.getMethod()));

        outHeaders.putIfAbsent(HTTP_URI, Optional.ofNullable(inHeaders.get(HTTP_URI))
                .orElse(outboundUri(filename)));

        outHeaders.putIfAbsent(ACCEPT, Optional.ofNullable(inHeaders.get(ACCEPT))
                .orElse(httpProperties.getAcceptHeader()));

        outHeaders.putIfAbsent(AUTHORIZATION, Optional.ofNullable(inHeaders.get(AUTHORIZATION))
                .orElse(authHeader()));
    }

    private final String outboundUri(Object filename) {
        return UriComponentsBuilder.fromUriString(String.format("http://%s", httpProperties.getHostAndPort()))
                .path(httpProperties.getPath())
                .buildAndExpand(filename)
                .toUriString();
    }

    private final String authHeader() {
        String auth = httpProperties.getUsername() + ":" + httpProperties.getPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(ISO_8859_1));

        return "Basic " + new String(encodedAuth, UTF_8);
    }
}
