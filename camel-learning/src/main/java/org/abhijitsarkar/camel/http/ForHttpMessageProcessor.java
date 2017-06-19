package org.abhijitsarkar.camel.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
public class ForHttpMessageProcessor implements org.apache.camel.Processor {
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

        Object filename = inHeaders.get(Exchange.FILE_NAME);

        if (Objects.isNull(filename)) {
            filename = inHeaders.get(S3Constants.KEY);

            if (Objects.isNull(filename)) {
                filename = defaultFilename();
            }
        }
        out.setHeader(Exchange.FILE_NAME, filename);

        if (!outHeaders.containsKey(Exchange.CONTENT_TYPE)) {
            out.setHeader(Exchange.CONTENT_TYPE, Optional.ofNullable(inHeaders.get(Exchange.CONTENT_TYPE))
                    .orElse(httpProperties.getContentTypeHeader()));
        }

        if (!outHeaders.containsKey(Exchange.HTTP_METHOD)) {
            out.setHeader(Exchange.HTTP_METHOD, Optional.ofNullable(inHeaders.get(Exchange.HTTP_METHOD))
                    .orElse(httpProperties.getMethod()));
        }

        if (!outHeaders.containsKey(Exchange.HTTP_URI)) {
            out.setHeader(Exchange.HTTP_URI, Optional.ofNullable(inHeaders.get(Exchange.HTTP_URI))
                    .orElse(outboundUri(filename)));
        }

        if (!outHeaders.containsKey(ACCEPT)) {
            out.setHeader(ACCEPT, Optional.ofNullable(inHeaders.get(ACCEPT))
                    .orElse(httpProperties.getAcceptHeader()));
        }

        if (!outHeaders.containsKey(AUTHORIZATION)) {
            out.setHeader(AUTHORIZATION, Optional.ofNullable(inHeaders.get(AUTHORIZATION))
                    .orElse(authHeader()));
        }
    }

    private final String defaultFilename() {
        return DateTimeFormatter.ofPattern("yyyyMMdd-kkmm").format(LocalDateTime.now()) + ".out";
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
