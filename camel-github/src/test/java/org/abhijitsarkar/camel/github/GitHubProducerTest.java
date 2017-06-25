package org.abhijitsarkar.camel.github;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class, GitHubProducerTest.TestConfiguration.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GitHubProducerTest {
    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private GitHubClient gitHubClient;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @After
    public void afterEach() {
        reset(gitHubClient);
    }

    @Test
    public void testGetCommitsForARepo() {
        producerTemplate.sendBodyAndHeader("direct:start", "whatever",
                "endpoint", "commits/test/test?username=test&password=test");

        ArgumentCaptor<GitHubRequest> argument = ArgumentCaptor.forClass(GitHubRequest.class);
        verify(gitHubClient).getCommitsForARepo(argument.capture(), eq(20));

        verifyGitHubRequest(argument.getValue());
    }

    @Test
    public void testGetAllFilesForACommit() {
        producerTemplate.sendBodyAndHeader("direct:start", "whatever",
                "endpoint", "files/test/test?username=test&password=test&sha=test");

        ArgumentCaptor<GitHubRequest> argument = ArgumentCaptor.forClass(GitHubRequest.class);
        verify(gitHubClient).getAllFilesForACommit(argument.capture(), eq("test"));

        verifyGitHubRequest(argument.getValue());
    }

    @Test
    public void testHystrixFallback() throws InterruptedException {
        when(gitHubClient.getCommitsForARepo(any(GitHubRequest.class), anyInt()))
                .thenThrow(new RuntimeException("boom!"));
        resultEndpoint.expectedBodiesReceived("fallback");

        producerTemplate.sendBodyAndHeader("direct:start", "whatever",
                "endpoint", "commits/test/test?username=test&password=test");

        verify(gitHubClient).getCommitsForARepo(any(GitHubRequest.class), anyInt());

        resultEndpoint.assertIsSatisfied();
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

                    from("direct:start")
                            .hystrix()
                            .hystrixConfiguration()
                            .circuitBreakerEnabled(false)
                            .end()
                            .toD("github:$simple{in.header.endpoint}")
                            .onFallback()
                            .transform().constant(singletonList("fallback"))
                            .end()
                            // Useless with Hystrix fallback enabled
//                            .onException(RuntimeException.class)
//                            .maximumRedeliveries(2)
//                            .redeliveryDelay(0)
                            .to("mock:result");
                }
            };
        }

        @Bean
        public GitHubClient mockGitHubClient() {
            return mock(GitHubClient.class);
        }
    }
}
