package org.abhijitsarkar.camel.github.consumer;

import org.abhijitsarkar.camel.github.Application;
import org.abhijitsarkar.camel.github.GitHub;
import org.abhijitsarkar.camel.github.GitHubTestUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicy;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class, GitHubConsumerTest.TestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GitHubConsumerTest {
    @EndpointInject(uri = "mock:result1")
    private MockEndpoint resultEndpoint1;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint resultEndpoint2;

    @EndpointInject(uri = "mock:result3")
    private MockEndpoint resultEndpoint3;

    @Autowired
    private CamelContext context;

    @Autowired
    private GitHub gitHubClient;

    @Before
    public void beforeEach() {
        GitHubTestUtil.configureMock(gitHubClient);
    }

    @After
    public void afterEach() {
        reset(gitHubClient);
    }

    @Test
    public void testGetRepos() throws InterruptedException {
        resultEndpoint1.expectedMessageCount(1);

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1).wereSentTo("mock:result1")
                .create();

        boolean done = notify.matches(5, TimeUnit.SECONDS);

        assertThat(done).isTrue();

        resultEndpoint1.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof List)) {
                return false;
            }

            List<GitHub.Repository> repos = (List<GitHub.Repository>) body;

            return repos.stream()
                    .allMatch(repo -> repo.getName().startsWith("mockRepo"));
        });

        resultEndpoint1.assertIsSatisfied();
    }

    @Test
    public void testGetCommits() throws InterruptedException {
        resultEndpoint2.expectedMessageCount(1);

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1).wereSentTo("mock:result2")
                .create();

        boolean done = notify.matches(5, TimeUnit.SECONDS);

        assertThat(done).isTrue();

        resultEndpoint2.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof List)) {
                return false;
            }

            List<GitHub.Commit> commits = (List<GitHub.Commit>) body;

            return commits.stream()
                    .allMatch(commit -> commit.getSha().startsWith("mockSha"));
        });

        resultEndpoint2.assertIsSatisfied();
    }

    @Test
    public void testGetCommit() throws InterruptedException {
        resultEndpoint3.expectedMessageCount(1);

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1).wereSentTo("mock:result3")
                .create();

        boolean done = notify.matches(5, TimeUnit.SECONDS);

        assertThat(done).isTrue();

        resultEndpoint3.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof GitHub.Commit)) {
                return false;
            }

            GitHub.Commit commit = (GitHub.Commit) body;

            return commit.getSha().startsWith("mockSha");
        });

        resultEndpoint3.assertIsSatisfied();
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public RouteBuilder testRoute() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    getContext().setTracing(true);
                    getContext().addRoutePolicyFactory(new RoutePolicyFactory() {
                        @Override
                        public RoutePolicy createRoutePolicy(CamelContext camelContext, String routeId, RouteDefinition route) {
                            return new StopAfterSuccessfulExchangeRoutePolicy();
                        }
                    });

                    from("github:users/username/repos?initialDelay=500")
                            .id("reposRoute")
                            .to("mock:result1");

                    from("github:repos/username/mockRepo1/commits?initialDelay=500")
                            .id("commitsRoute")
                            .to("mock:result2");

                    from("github:repos/username/mockRepo1/commits/mockSha1?initialDelay=500")
                            .id("commitRoute")
                            .to("mock:result3");
                }
            };
        }

        @Bean
        public GitHub mockGitHubClient() {
            return mock(GitHub.class);
        }
    }
}
