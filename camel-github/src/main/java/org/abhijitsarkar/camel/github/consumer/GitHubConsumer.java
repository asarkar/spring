package org.abhijitsarkar.camel.github.consumer;

import org.abhijitsarkar.camel.github.GitHubEndpoint;
import org.abhijitsarkar.camel.github.GitHubUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * @author Abhijit Sarkar
 */
public class GitHubConsumer extends ScheduledPollConsumer {
    private final GitHubEndpoint endpoint;

    public GitHubConsumer(GitHubEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();

        GitHubUtil.execute(endpoint, exchange);

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }

        return 1;
    }
}
