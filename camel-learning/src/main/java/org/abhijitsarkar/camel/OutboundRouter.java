package org.abhijitsarkar.camel;

import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.camel.http.HttpProperties;
import org.apache.camel.ExchangeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.abhijitsarkar.camel.Application.DEFAULT_PROFILE;
import static org.abhijitsarkar.camel.Application.OUTBOUND_HTTP_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Component
@Slf4j
public class OutboundRouter {
    private static final String OUTBOUND_PROFILES_INVOKED = "invoked";

    @Autowired(required = false)
    private HttpProperties httpProperties;

    @Autowired
    private Environment env;

    public String findRoute(@ExchangeProperties Map<String, Object> properties) {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        // Get the state from the exchange properties
        List<String> invoked = (List<String>) properties.getOrDefault(OUTBOUND_PROFILES_INVOKED, new ArrayList<>());
        List<String> routes = new ArrayList<>();

        if (activeProfiles.contains(OUTBOUND_HTTP_PROFILE) && !invoked.contains(OUTBOUND_HTTP_PROFILE)) {
            Assert.notNull(httpProperties, "HttpProperties must not be null.");

            String outboundHttpUri = UriComponentsBuilder.fromUriString("http4://notused")
                    .queryParam("disableStreamCache", true)
                    .queryParam("httpClient.socketTimeout", httpProperties.getReadTimeoutMillis())
                    .queryParam("httpClient.connectTimeout", httpProperties.getConnectTimeoutMillis())
                    .build()
                    .toUriString();

            invoked.add(OUTBOUND_HTTP_PROFILE);
            routes.add(outboundHttpUri);
        }

        if (activeProfiles.contains(DEFAULT_PROFILE) && !invoked.contains(DEFAULT_PROFILE)) {
            String outboundFileUri = UriComponentsBuilder.fromUriString("file://build")
                    .queryParam("autoCreate", true)
                    .queryParam("eagerDeleteTargetFile", true)
                    .queryParam("tempFileName", "${date:now:yyyyMMdd-kkmmss}.tmp")
                    .build()
                    .toUriString();

            invoked.add(DEFAULT_PROFILE);
            routes.add(outboundFileUri);
        }

        // Store the state back on the properties
        properties.put(OUTBOUND_PROFILES_INVOKED, invoked);

        log.info("Routes found: {}.", routes);

        Assert.state(routes.size() <= 1,
                String.format("Multiple routes found: %s. Only one outbound route can be enabled.", routes));

        return routes.isEmpty() ? null : routes.stream().collect(joining(","));
    }
}
