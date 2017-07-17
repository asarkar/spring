package org.abhijitsarkar;

import kotlin.Pair;
import org.abhijitsarkar.client.GitLabClient;
import org.abhijitsarkar.client.GitLabProperties;
import org.abhijitsarkar.client.Group;
import org.abhijitsarkar.domain.License;
import org.abhijitsarkar.service.JGitAgent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.abhijitsarkar.TestUtilKt.projectDir;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {Application.class, ApplicationTest.TestConfiguration.class}
)
public class ApplicationTest {
    @Autowired
    private LicenseGeneratedEventListener eventListener;

    @Test
    public void testEndToEnd() {
        Map<String, Collection<License>> licenses = eventListener.event.getLicenses();

        assertNotNull(licenses);

        boolean containsSpring = licenses.getOrDefault("test-project", emptyList())
                .stream()
                .anyMatch(license -> license.getComponents().stream().anyMatch(c -> c.contains("spring")));
        assertTrue(containsSpring);
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        @Primary
        @SuppressWarnings("unchecked")
        GitLabClient gitLabClient() {
            GitLabClient gitLabClient = mock(GitLabClient.class);

            Group.Project project = new Group.Project();
            project.setSshUrl(Paths.get(projectDir().getAbsolutePath(), "test-project").toUri().toString());
            project.setName("test-project");

            when(gitLabClient.projects(any(Pair.class)))
                    .thenReturn(Flux.just(new Pair<String, Group.Project>("test-group", project)));

            return gitLabClient;
        }

        @Bean
        @Primary
        JGitAgent jGitAgent() {
            JGitAgent jGitAgent = mock(JGitAgent.class);

            when(jGitAgent.clone(any(Group.Project.class), any(GitLabProperties.GroupProperties.class)))
                    .thenReturn(Mono.just(new File(projectDir(), "test-project")));

            return jGitAgent;
        }

        @Bean
        LicenseGeneratedEventListener licenseGeneratedEventListener() {
            return new LicenseGeneratedEventListener();
        }
    }

    static class LicenseGeneratedEventListener {
        private LicenseGeneratedEvent event;

        @EventListener(LicenseGeneratedEvent.class)
        void listenToLicenseGeneratedEvent(LicenseGeneratedEvent event) {
            this.event = event;
        }
    }
}
