package org.abhijitsarkar;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Abhijit Sarkar
 */
public interface TestApplicationContextInitializer extends ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    default void initialize(ConfigurableApplicationContext appCtx) {
        try {
            Map<String, Object> props = new HashMap<>();
            // should be <some-path>/template-resolver/build/classes/test/
            File test = new File(getClass().getResource("/").toURI());
            File resources = new File(test.getParentFile().getParentFile().getParent(), "src/test/resources");
            props.put("templates.baseUri", getBaseUri(resources));
            props.put("template.names", "nginx-app");
            MapPropertySource mapPropertySource = new MapPropertySource("test-props", props);
            appCtx.getEnvironment().getPropertySources().addFirst(mapPropertySource);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    String getBaseUri(File resources);
}
