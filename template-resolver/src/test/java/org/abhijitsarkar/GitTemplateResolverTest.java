package org.abhijitsarkar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Abhijit Sarkar
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = GitAwareApplicationContextInitializer.class, classes = TemplateResolverApp.class)
@ActiveProfiles("default")
public class GitTemplateResolverTest {
    @Autowired
    private TemplateResolverSvc templateResolverSvc;

    @Test
    public void contextLoads() {
        assertThat(true, is(true));
    }
}