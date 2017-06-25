package org.abhijitsarkar.camel.github;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.abhijitsarkar.camel.github.producer.GitHubProducer;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.StringHelper;
import org.springframework.util.StringUtils;

/**
 * @author Abhijit Sarkar
 */
@UriEndpoint(scheme = "github", title = "GitHub", syntax = "github:type/repo/owner/branch", label = "api,file")
@Data
@EqualsAndHashCode(callSuper = false)
public class GitHubEndpoint extends DefaultEndpoint {
    private static final String DEFAULT_LIMIT = "20";
    private static final String DEFAULT_BRANCH = "master";

    public GitHubEndpoint(String uri, GitHubComponent component) {
        super(uri, component);
    }

    @UriPath
    @Metadata(required = "true")
    private GitHubType type;
    @UriPath
    @Metadata(required = "true")
    private String repo;
    @UriPath
    @Metadata(required = "true")
    private String owner;
    @UriPath(defaultValue = DEFAULT_BRANCH)
    private String branch;

    @UriParam
    @Metadata(required = "true")
    private String username;
    @UriParam
    @Metadata(required = "true")
    private String password;
    @UriParam(defaultValue = DEFAULT_LIMIT, label = "producer")
    private int limit;
    @UriParam(label = "producer")
    private String sha;

    @Override
    public Producer createProducer() throws Exception {
        StringHelper.notEmpty(repo, "repo");
        StringHelper.notEmpty(owner, "owner");
        StringHelper.notEmpty(username, "username");
        StringHelper.notEmpty(password, "password");

        if (type == GitHubType.FILES) {
            StringHelper.notEmpty(sha, "sha");
        }

        if (StringUtils.isEmpty(branch)) {
            branch = DEFAULT_BRANCH;
        }
        if (limit == 0) {
            limit = Integer.parseInt(DEFAULT_LIMIT);
        }
        return new GitHubProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
