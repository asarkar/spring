package org.abhijitsarkar.camel.github;

import org.abhijitsarkar.camel.github.consumer.StopAfterSuccessfulExchangeRoutePolicy;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicy;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class, GitHubConsumerTest.TestConfiguration.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GitHubConsumerTest {
    @EndpointInject(uri = "mock:result1")
    private MockEndpoint resultEndpoint1;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint resultEndpoint2;

    @Autowired
    private CamelContext context;

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    public void testGetCommitsForARepo() throws InterruptedException {
        when(gitHubClient.getCommitsForARepo(any(GitHubRequest.class), anyInt()))
                .thenReturn(Collections.singletonList("test"));
        resultEndpoint1.expectedBodiesReceived("test");

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1).wereSentTo("mock:result1")
                .create();

        boolean done = notify.matches(5, TimeUnit.SECONDS);
        assertThat(done).isTrue();

        ArgumentCaptor<GitHubRequest> argument = ArgumentCaptor.forClass(GitHubRequest.class);
        verify(gitHubClient).getCommitsForARepo(argument.capture(), eq(20));

        verifyGitHubRequest(argument.getValue());

        resultEndpoint1.assertIsSatisfied();
    }

    @Test
    public void testGetAllFilesForACommit() throws InterruptedException {
        when(gitHubClient.getAllFilesForACommit(any(GitHubRequest.class), anyString()))
                .thenReturn(Collections.singletonList("test"));
        resultEndpoint2.expectedBodiesReceived("test");

        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1).wereSentTo("mock:result2")
                .create();

        boolean done = notify.matches(5, TimeUnit.SECONDS);
        assertThat(done).isTrue();

        ArgumentCaptor<GitHubRequest> argument = ArgumentCaptor.forClass(GitHubRequest.class);
        verify(gitHubClient).getAllFilesForACommit(argument.capture(), eq("test"));

        verifyGitHubRequest(argument.getValue());

        resultEndpoint2.assertIsSatisfied();
    }

    private final void verifyGitHubRequest(GitHubRequest request) {
        assertThat(request.getBranch()).isEqualTo("master");
        assertThat(request.getRepo()).isEqualTo("test");
        assertThat(request.getOwner()).isEqualTo("test");
        assertThat(request.getPassword()).isEqualTo("test");
        assertThat(request.getUsername()).isEqualTo("test");
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

                    from("github:commits/test/test?username=test&password=test&initialDelay=500")
                            .id("commitsRoute")
                            .to("mock:result1");

                    from("github:files/test/test?username=test&password=test&sha=test&initialDelay=500")
                            .id("filesRoute")
                            .to("mock:result2");
                }
            };
        }

        @Bean
        public GitHubClient mockGitHubClient() {
            return mock(GitHubClient.class);
        }
    }
}
