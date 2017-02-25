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
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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

    static {
        System.setProperty("rc.name", "testNative");
    }

    @Test
    public void testResolves() throws URISyntaxException, IOException {
        templateResolverSvc.resolve();

        File test = new File(getClass().getResource("/").toURI());
        String projectDir = test.getParentFile().getParentFile().getParent();

        Path out = Paths.get(projectDir, "build", "native.yaml");
        assertThat(Files.exists(out), is(true));

        Map<String, Long> map = Files.lines(out)
                .map(String::trim)
                .filter(line -> line.endsWith(": test")
                        || line.endsWith(": testNative"))
                .collect(groupingBy(line -> line.split(": ")[1], counting()));

        assertThat(map.get("test"), is(2L));
        assertThat(map.get("testNative"), is(1L));
    }
}