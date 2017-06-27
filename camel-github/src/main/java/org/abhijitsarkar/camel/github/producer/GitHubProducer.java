package org.abhijitsarkar.camel.github.producer;

import org.abhijitsarkar.camel.github.GitHubEndpoint;
import org.abhijitsarkar.camel.github.GitHubUtil;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * @author Abhijit Sarkar
 */
public class GitHubProducer extends DefaultProducer {
    private final GitHubEndpoint endpoint;

    public GitHubProducer(GitHubEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        GitHubUtil.execute(endpoint, exchange);
    }
}
