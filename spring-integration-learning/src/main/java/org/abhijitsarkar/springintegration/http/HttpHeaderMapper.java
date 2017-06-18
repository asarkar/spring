package org.abhijitsarkar.springintegration.http;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * @author Abhijit Sarkar
 */
public class HttpHeaderMapper implements HeaderMapper<HttpHeaders> {
    private final DefaultHttpHeaderMapper delegate;
    private final HttpProperties httpProperties;

    public HttpHeaderMapper(HttpProperties httpProperties) {
        delegate = new DefaultHttpHeaderMapper();
        delegate.setInboundHeaderNames(new String[]{LOCATION});

        this.httpProperties = httpProperties;
    }

    @Override
    public void fromHeaders(MessageHeaders headers, HttpHeaders target) {
        delegate.fromHeaders(headers, target);

        if (!target.containsKey(ACCEPT)) {
            target.add(ACCEPT, httpProperties.getAcceptHeader());
        }

        if (!target.containsKey(CONTENT_TYPE)) {
            target.add(CONTENT_TYPE, httpProperties.getContentTypeHeader());
        }

        if (!target.containsKey(AUTHORIZATION)) {
            String auth = httpProperties.getUsername() + ":" + httpProperties.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth, UTF_8);

            target.add(AUTHORIZATION, authHeader);
        }
    }

    @Override
    public Map<String, Object> toHeaders(HttpHeaders source) {
        return delegate.toHeaders(source);
    }
}
