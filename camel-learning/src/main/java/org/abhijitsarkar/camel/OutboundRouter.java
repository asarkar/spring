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

/**
 * @author Abhijit Sarkar
 */
@Component
@Slf4j
public class OutboundRouter {
    @Autowired(required = false)
    private HttpProperties httpProperties;

    @Autowired
    private Environment env;

    public String findRoute(@ExchangeProperties Map<String, Object> properties) {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        // Get the state from the exchange properties
        List<String> invoked = (List<String>) properties.getOrDefault("invoked", new ArrayList<>());
        List<String> routes = new ArrayList<>();

        if (activeProfiles.contains("outbound-http") && !invoked.contains("outbound-http")) {
            Assert.notNull(httpProperties, "HttpProperties must not be null.");

            String outboundHttpUri = UriComponentsBuilder.fromUriString("http4://notused")
                    .queryParam("disableStreamCache", true)
                    .queryParam("httpClient.socketTimeout", httpProperties.getReadTimeoutMillis())
                    .queryParam("httpClient.connectTimeout", httpProperties.getConnectTimeoutMillis())
                    .build()
                    .toUriString();

            invoked.add("outbound-http");
            routes.add(outboundHttpUri);
        }

        if (activeProfiles.contains("default") && !invoked.contains("default")) {
            String outboundFileUri = UriComponentsBuilder.fromUriString("file://build")
                    .queryParam("autoCreate", true)
                    .queryParam("eagerDeleteTargetFile", true)
                    .queryParam("tempFileName", "${date:now:yyyyMMdd-kkmmss}.tmp")
                    .build()
                    .toUriString();

            invoked.add("default");
            routes.add(outboundFileUri);
        }

        // Store the state back on the properties
        properties.put("invoked", invoked);

        log.info("Routes found: {}.", routes);

        Assert.state(routes.size() <= 1,
                String.format("Multiple routes found: %s. Only one outbound route can be enabled.", routes));

        return routes.isEmpty() ? null : routes.stream().collect(joining(","));
    }
}
