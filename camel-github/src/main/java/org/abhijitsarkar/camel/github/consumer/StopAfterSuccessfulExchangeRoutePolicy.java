package org.abhijitsarkar.camel.github.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class StopAfterSuccessfulExchangeRoutePolicy extends RoutePolicySupport {
    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        super.onExchangeDone(route, exchange);

        try {
            log.info("Stopping route: {}.", route.getId());
            exchange.getContext().stopRoute(route.getId(), 5, SECONDS);
        } catch (Exception e) {
            log.error("Failed to stop route: {}.", route.getId(), e);
        }
    }
}
