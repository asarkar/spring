package org.abhijitsarkar.camel.github;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * @author Abhijit Sarkar
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GitHubUtil {
    public static void execute(GitHubEndpoint endpoint, Exchange exchange) {
        Set<GitHub> gitHubClients = endpoint.getCamelContext().getRegistry().findByType(GitHub.class);

        GitHub gitHubClient = null;

        if (gitHubClients.isEmpty()) {
            exchange.setException(new IllegalStateException(String.format("Class %s not found", GitHub.class.getName())));
        } else {
            gitHubClient = gitHubClients.iterator().next();

            switch (endpoint.getType()) {
                case REPOS:
                    if (StringUtils.isEmpty(endpoint.getSha())) {
                        List<GitHub.Commit> commits = gitHubClient.commits(endpoint.getUsername(), endpoint.getRepo());
                        // http://camel.apache.org/using-getin-or-getout-methods-on-exchange.html
                        exchange.getIn().setBody(commits, List.class);
                    } else {
                        GitHub.Commit commit = gitHubClient.commit(endpoint.getUsername(), endpoint.getRepo(), endpoint.getSha());
                        exchange.getIn().setBody(commit, GitHub.Commit.class);
                    }

                    break;
                case USERS:
                    List<GitHub.Repository> repos = gitHubClient.repos(endpoint.getUsername());
                    exchange.getIn().setBody(repos, List.class);

                    break;
                default:
                    break;
            }
        }
    }
}
