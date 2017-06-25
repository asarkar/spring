package org.abhijitsarkar.camel.github;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * @author Abhijit Sarkar
 */
public class GitHubComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        GitHubEndpoint endpoint = new GitHubEndpoint(uri, this);
        setProperties(endpoint, parameters);

        String[] parts = remaining.split("/");

        switch (parts.length) {
            case 4:
                endpoint.setBranch(parts[3]);
            case 3:
                endpoint.setOwner(parts[2]);
            case 2:
                endpoint.setRepo(parts[1]);
            case 1:
                GitHubType type = getCamelContext().getTypeConverter().convertTo(GitHubType.class,
                        parts[0].toUpperCase());

                endpoint.setType(type);
                break;
            default:
                break;
        }

        return endpoint;
    }
}
