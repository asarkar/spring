package org.abhijitsarkar.camel.github.producer;

import org.abhijitsarkar.camel.github.Application;
import org.abhijitsarkar.camel.github.GitHub;
import org.abhijitsarkar.camel.github.GitHubTestUtil;
import org.abhijitsarkar.camel.github.consumer.StopAfterSuccessfulExchangeRoutePolicy;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class, GitHubProducerTest.TestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GitHubProducerTest {
    @EndpointInject(uri = "mock:result1")
    private MockEndpoint resultEndpoint1;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint resultEndpoint2;

    @EndpointInject(uri = "mock:result3")
    private MockEndpoint resultEndpoint3;

    @Autowired
    private ProducerTemplate producerTemplate;

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

        producerTemplate.sendBody("direct:startReposTest", "whatever");

        resultEndpoint1.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof List)) {
                return false;
            }

            List<GitHub.Repository> repos = (List<GitHub.Repository>) body;

            return repos.stream()
                    .allMatch(repo -> repo.getName().startsWith("mockRepo"));
        });

        resultEndpoint1.assertIsSatisfied(5000l);
    }

    @Test
    public void testGetCommits() throws InterruptedException {
        resultEndpoint2.expectedMessageCount(1);

        producerTemplate.sendBody("direct:startCommitsTest", "whatever");

        resultEndpoint2.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof List)) {
                return false;
            }

            List<GitHub.Commit> commits = (List<GitHub.Commit>) body;

            return commits.stream()
                    .allMatch(commit -> commit.getSha().startsWith("mockSha"));
        });

        resultEndpoint2.assertIsSatisfied(5000l);
    }

    @Test
    public void testGetCommit() throws InterruptedException {
        resultEndpoint3.expectedMessageCount(1);

        producerTemplate.sendBody("direct:startCommitTest", "whatever");

        resultEndpoint3.expectedMessagesMatches(exchange -> {
            Object body = exchange.getIn().getBody();

            if (!(body instanceof GitHub.Commit)) {
                return false;
            }

            GitHub.Commit commit = (GitHub.Commit) body;

            return commit.getSha().startsWith("mockSha");
        });

        resultEndpoint3.assertIsSatisfied(5000l);
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

                    from("direct:startReposTest")
                            .id("reposRoute")
                            .to("github:users/username/repos")
                            .to("mock:result1");

                    from("direct:startCommitsTest")
                            .id("commitsRoute")
                            .to("github:repos/username/mockRepo1/commits")
                            .to("mock:result2");

                    from("direct:startCommitTest")
                            .id("commitRoute")
                            .to("github:repos/username/mockRepo1/commits/mockSha1")
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
