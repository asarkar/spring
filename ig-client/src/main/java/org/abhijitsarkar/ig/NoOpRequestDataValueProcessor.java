package org.abhijitsarkar.ig;

import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * @author Abhijit Sarkar
 */
public class NoOpRequestDataValueProcessor implements RequestDataValueProcessor {
    @Override
    public String processAction(ServerWebExchange exchange, String action, String httpMethod) {
        return action;
    }

    @Override
    public String processFormFieldValue(ServerWebExchange exchange, String name, String value, String type) {
        return value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(ServerWebExchange exchange) {
        return emptyMap();
    }

    @Override
    public String processUrl(ServerWebExchange exchange, String url) {
        return url;
    }
}
