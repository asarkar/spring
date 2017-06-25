package org.abhijitsarkar.camel.github.consumer;

import org.abhijitsarkar.camel.github.GitHubClient;
import org.abhijitsarkar.camel.github.GitHubClientImpl;
import org.abhijitsarkar.camel.github.GitHubEndpoint;
import org.abhijitsarkar.camel.github.GitHubRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import java.util.List;
import java.util.Set;

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
        Set<GitHubClient> gitHubClients = getEndpoint().getCamelContext().getRegistry().findByType(GitHubClient.class);

        GitHubClient gitHubClient = null;

        if (gitHubClients.isEmpty()) {
            gitHubClient = new GitHubClientImpl();
        } else {
            gitHubClient = gitHubClients.iterator().next();
        }

        GitHubRequest gitHubRequest = GitHubRequest.builder()
                .branch(endpoint.getBranch())
                .owner(endpoint.getOwner())
                .repo(endpoint.getRepo())
                .username(endpoint.getUsername())
                .password(endpoint.getPassword())
                .build();

        Exchange exchange = endpoint.createExchange();

        switch (endpoint.getType()) {
            case COMMITS:
                List<String> commits = gitHubClient.getCommitsForARepo(gitHubRequest, endpoint.getLimit());
                // http://camel.apache.org/using-getin-or-getout-methods-on-exchange.html
                exchange.getIn().setBody(commits, List.class);
                break;
            case FILES:
                // http://camel.apache.org/using-getin-or-getout-methods-on-exchange.html
                List<String> files = gitHubClient.getAllFilesForACommit(gitHubRequest, endpoint.getSha());
                exchange.getIn().setBody(files, List.class);
                break;
            default:
                break;
        }

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }

        return 1;
    }
}
