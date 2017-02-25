package org.abhijitsarkar;

import org.junit.Test;
import org.springframework.beans.factory.config.YamlMapFactoryBean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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
        YamlMapFactoryBean yamlMapFactoryBean = ThymeleafConfig.yamlPropertiesFactory(resources);
        Map<String, Object> props = yamlMapFactoryBean.getObject();

        assertThat(props.containsKey("app"), is(true));
        assertThat(((Map<String, Object>) props.get("app")).get("name"), is("test"));
    }
}