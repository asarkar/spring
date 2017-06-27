package org.abhijitsarkar.camel.github;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
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
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.abhijitsarkar.camel.github.Application.ENDPOINT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @author Abhijit Sarkar
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class, GitHubRouteTest.TestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GitHubRouteTest {
    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private GitHub gitHubClient;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Before
    public void beforeEach() {
        GitHubTestUtil.configureMock(gitHubClient);
    }

    @After
    public void afterEach() {
        reset(gitHubClient);
    }

    @Test
    public void testGitHubRoute() throws InterruptedException {
        resultEndpoint.expectedMessageCount(2);

        resultEndpoint.expectedMessagesMatches(exchange -> {
            List<GitHub.Commit> commits = exchange.getIn().getBody(List.class);

            return !CollectionUtils.isEmpty(commits)
                    && commits.size() == 2
                    && commits.stream().allMatch(c -> c.getSha().startsWith("mockSha"));
        });

        producerTemplate.sendBodyAndHeader("direct:start", "username", ENDPOINT, "mock:result");

        resultEndpoint.assertIsSatisfied(5000l);
    }

    @Configuration
    public static class TestConfiguration {
//        @Bean
//        public RouteBuilder testRoute() {
//            return new RouteBuilder() {
//                @Override
//                public void configure() throws Exception {
//                    getContext().setTracing(true);
//
//                    from("direct:start")
//                            .hystrix()
//                            .hystrixConfiguration()
//                            .circuitBreakerEnabled(false)
//                            .end()
//                            .toD("github:$simple{in.header.endpoint}")
//                            .onFallback()
//                            .transform().constant(singletonList("fallback"))
//                            .end()
//                            // Useless with Hystrix fallback enabled
//                            .onException(RuntimeException.class)
//                            .maximumRedeliveries(2)
//                            .redeliveryDelay(0)
//                            .to("mock:result");
//                }
//            };
//        }

        @Bean
        public GitHub mockGitHubClient() {
            return mock(GitHub.class);
        }
    }
}
