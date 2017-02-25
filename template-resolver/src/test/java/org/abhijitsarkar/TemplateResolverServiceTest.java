package org.abhijitsarkar;

import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.deleteIfExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class TemplateResolverServiceTest {
    private Path out;
    private ITemplateEngine templateEngine;
    String projectDir;
    private TemplateResolverService service;

    @Before
    public void before() throws URISyntaxException {
        templateEngine = mock(ITemplateEngine.class);

        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> properties2 = new HashMap<>();
        properties2.put("name", "test");
        properties.put("app", properties2);

        service = new TemplateResolverService(templateEngine, properties);

        File test = new File(getClass().getResource("/").toURI());
        projectDir = test.getParentFile().getParentFile().getParent();
        out = Paths.get(projectDir, "build", "nginx-app.yaml");
        service.output = out.toFile().getAbsolutePath();
    }

    @Test
    public void testResolvesSingleExistingTemplate() throws IOException {
        service.names = "whatever";

        when(templateEngine.process(eq("whatever"), any(Context.class)))
                .thenReturn("success");

        service.resolve();

        Path out = Paths.get(projectDir, "build", "nginx-app.yaml");
        assertThat(Files.exists(out), is(true));

        boolean isPresent = Files.lines(out)
                .map(String::trim)
                .filter(line -> line.equalsIgnoreCase("success"))
                .findAny()
                .isPresent();

        assertThat(isPresent, is(true));
    }

    @Test
    public void testHandlesNonExistingTemplate() throws IOException {
        service.names = "whatever";

        when(templateEngine.process(eq("whatever"), any(Context.class)))
                .thenThrow(new RuntimeException("test"));
        Path out = Paths.get(projectDir, "build", "nginx-app.yaml");
        deleteIfExists(out);

        service.resolve();

        assertThat(Files.exists(out), is(false));
    }

    @Test
    public void testHandlesExistingAndNonExistingTemplate() throws IOException {
        service.names = "a, b";

        when(templateEngine.process(eq("a"), any(Context.class)))
                .thenReturn("success");
        when(templateEngine.process(eq("b"), any(Context.class)))
                .thenThrow(new RuntimeException("test"));

        Path out = Paths.get(projectDir, "build", "nginx-app.yaml");
        deleteIfExists(out);

        service.resolve();

        boolean isPresent = Files.lines(out)
                .map(String::trim)
                .filter(line -> line.equalsIgnoreCase("success"))
                .findAny()
                .isPresent();

        assertThat(isPresent, is(true));
    }
}