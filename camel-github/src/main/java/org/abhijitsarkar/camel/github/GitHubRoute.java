package org.abhijitsarkar.camel.github;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.abhijitsarkar.camel.github.Application.ENDPOINT;
import static org.abhijitsarkar.camel.github.Application.REPO;
import static org.abhijitsarkar.camel.github.Application.USERNAME;
import static org.apache.camel.builder.PredicateBuilder.isNotNull;

/**
 * @author Abhijit Sarkar
 */
@Component
@Slf4j
public class GitHubRoute extends RouteBuilder {
    private static final int LIMIT = 5;

    @Override
    public void configure() throws Exception {
        from("direct:start")
                .id("usersRoute")
                .filter(isNotNull(simple("${header." + ENDPOINT + "}")))
                .setHeader(USERNAME, simple("${body}"))
                .toD("github:users/${body}/repos")
                .process(e -> this.<GitHub.Repository>limitList(e))
                .to("direct:reposRoute1");

        from("direct:reposRoute1")
                .id("reposRoute1")
                .split(body())
                .parallelProcessing()
                .setHeader(REPO, simple("${body.name}"))
                .toD("github:repos/${header." + USERNAME + "}" + "/${body.name}/commits")
                .process(e -> this.<GitHub.Commit>limitList(e))
                .to("direct:reposRoute2");

        from("direct:reposRoute2")
                .id("reposRoute2")
                .split(body())
                .toD("github:repos/${header." + USERNAME + "}" + "/${header." + REPO + "}" + "/commits/${body.sha}")
                .process(e -> {
                    GitHub.Commit commit = e.getIn().getBody(GitHub.Commit.class);

                    flattenAndLimitFiles(commit);
                })
                // http://camel.apache.org/aggregator2.html
                .aggregate(header(REPO), new AggregateByRepoStrategy())
                .forceCompletionOnStop()
                .eagerCheckCompletion()
                .completionPredicate(header("CamelSplitComplete").convertTo(Boolean.class).isEqualTo(TRUE))
                .toD("${header." + ENDPOINT + "}");

        from("direct:end")
                .process().exchange(PrettyPrinter::print);
    }

    private void flattenAndLimitFiles(GitHub.Commit commit) {
        List<GitHub.Commit.File> files = commit.getFiles();
        if (!CollectionUtils.isEmpty(files) && files.size() > LIMIT) {
            commit.setFiles(files.subList(0, LIMIT));
        }

        GitHub.Commit innerCommit = commit.getCommit();
        GitHub.Commit.Committer committer = innerCommit.getCommitter();

        GitHub.Commit.Committer newCommitter = new GitHub.Commit.Committer();
        BeanUtils.copyProperties(committer, newCommitter);

        commit.setMessage(innerCommit.getMessage());
        commit.setCommitter(newCommitter);

        commit.setCommit(null);
    }

    private <T> void limitList(Exchange e) {
        List<T> commits = e.getIn().getBody(List.class);

        if (commits.size() > LIMIT) {
            e.getIn().setBody(commits.subList(0, LIMIT));
        }
    }

    private static final class AggregateByRepoStrategy extends AbstractListAggregationStrategy<GitHub.Commit> {
        @Override
        public GitHub.Commit getValue(Exchange exchange) {
            return exchange.getIn().getBody(GitHub.Commit.class);
        }
    }
}