package org.abhijitsarkar;

import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Abhijit Sarkar
 */
public class ThymeleafConfigTest {
    @Test
    public void testYamlPropertiesFactory() throws URISyntaxException, IOException {
        File test = new File(getClass().getResource("/").toURI());
        String projectDir = test.getParentFile().getParentFile().getParent();
        String resources = new File(projectDir, "src/test/resources").getAbsolutePath();
        YamlPropertiesFactoryBean propertiesFactory = ThymeleafConfig.yamlPropertiesFactory(resources,
                singletonList("nginx"));
        Properties props = propertiesFactory.getObject();

        assertThat(props.getProperty("app.name"), is("test"));
    }
}