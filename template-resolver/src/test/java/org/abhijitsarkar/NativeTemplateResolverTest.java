package org.abhijitsarkar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Abhijit Sarkar
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = NativeApplicationContextInitializer.class, classes = TemplateResolverApp.class)
@ActiveProfiles("native")
public class NativeTemplateResolverTest {
    @Autowired
    private TemplateResolverService templateResolverSvc;

    @Test
    public void testResolves() throws URISyntaxException, IOException {
        File test = new File(getClass().getResource("/").toURI());
        String projectDir = test.getParentFile().getParentFile().getParent();

        Path out = Paths.get(projectDir, "build", "nginx-app.yaml");
        assertThat(Files.exists(out), is(true));

        long count = Files.lines(out)
                .map(String::trim)
                .filter(line -> line.equalsIgnoreCase("name: test")
                        || line.equalsIgnoreCase("app: test"))
                .count();

        assertThat(count, is(3L));
    }
}