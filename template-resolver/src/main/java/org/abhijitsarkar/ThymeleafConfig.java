package org.abhijitsarkar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StringUtils.cleanPath;
import static org.springframework.util.StringUtils.getFilename;
import static org.thymeleaf.templatemode.TemplateMode.TEXT;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@Slf4j
public class ThymeleafConfig {
    @Configuration
    @Profile("default")
    static class GitTemplateResolverConfiguration {
        private String local = null;

        @Value("${templates.baseUri}")
        private String baseUri;
        @Value("${templates.git.localRepoPath:}")
        private String localRepoPath;
        @Value("${spring.application.name:UNKNOWN}")
        private String applicationName;

        @PostConstruct
        void postConstruct() throws IOException {
            log.debug("Using Git.");

            if (this.localRepoPath.isEmpty()) {
                local = Files.createTempDirectory(applicationName).toFile().getAbsolutePath();
            } else {
                local = this.localRepoPath;
            }
        }

        @Bean
        ITemplateResolver gitTemplateResolver() {
            return configureTemplateResolver(new GitTemplateResolver(baseUri, local), local);
        }

        @Bean
        YamlMapFactoryBean gitPropertiesFactory() throws IOException {
            return yamlPropertiesFactory(local);
        }
    }

    @Configuration
    @Profile("native")
    static class FileTemplateResolverConfiguration {
        @Value("${templates.baseUri}")
        private String baseUri;

        @PostConstruct
        void postConstruct() {
            log.debug("Using FileTemplateResolver.");
        }

        @Bean
        ITemplateResolver fileTemplateResolver() {
            return configureTemplateResolver(new FileTemplateResolver(), baseUri);
        }

        @Bean
        YamlMapFactoryBean nativePropertiesFactory() throws IOException {
            return yamlPropertiesFactory(baseUri);
        }
    }

    static YamlMapFactoryBean yamlPropertiesFactory(String path) throws IOException {
        YamlMapFactoryBean factory = new YamlMapFactoryBean();

        FileSystemResource[] resources = Files.find(Paths.get(path, "properties"),
                12,
                (p, attr) -> {
                    String filename = getFilename(cleanPath(p.toFile().getAbsolutePath()));

                    return filename.endsWith(".yaml") || filename.endsWith(".yml");
                })
                .map(Path::toFile)
                .peek(f -> log.debug("Found resource: {}.", f.getAbsolutePath()))
                .map(FileSystemResource::new)
                .toArray(size -> new FileSystemResource[size]);

        factory.setResources(resources);

        return factory;
    }

    static ITemplateResolver configureTemplateResolver(AbstractConfigurableTemplateResolver templateResolver, String path) {
        templateResolver.setPrefix(path + "/templates/");
        templateResolver.setSuffix(".template");
        templateResolver.setTemplateMode(TEXT);
        templateResolver.setCharacterEncoding(UTF_8.name());
        templateResolver.setCacheable(false);

        return templateResolver;
    }

    @Bean
    ITemplateEngine templateEngine(ITemplateResolver templateResolver) {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    @Bean
    TemplateResolverService templateResolverSvc(ITemplateEngine templateEngine, YamlMapFactoryBean factory) {
        return new TemplateResolverService(templateEngine, factory.getObject());
    }
}
