package org.abhijitsarkar.camel.github;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.abhijitsarkar.camel.github.consumer.GitHubConsumer;
import org.abhijitsarkar.camel.github.producer.GitHubProducer;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.StringHelper;

import java.util.Map;

/**
 * @author Abhijit Sarkar
 */
@UriEndpoint(scheme = "github", title = "GitHub", syntax = "github:endpoint")
@Data
@EqualsAndHashCode(callSuper = false)
public class GitHubEndpoint extends ScheduledPollEndpoint {
    public GitHubEndpoint(String uri, GitHubComponent component) {
        super(uri, component);
    }

    @UriPath
    @Metadata(required = "true")
    private GitHubType type;
    @UriPath
    @Metadata(required = "true")
    private String username;
    @UriPath
    private String repo;
    @UriPath
    private String sha;

    @Override
    public void configureProperties(Map<String, Object> options) {
        super.configureProperties(options);

        StringHelper.notEmpty(username, "username");

        if (type == GitHubType.REPOS) {
            StringHelper.notEmpty(repo, "repo");
        }
    }

    @Override
    public Producer createProducer() throws Exception {
        return new GitHubProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new GitHubConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
