package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StringUtils.cleanPath;
import static org.springframework.util.StringUtils.getFilename;
import static org.springframework.util.StringUtils.stripFilenameExtension;
import static org.thymeleaf.templatemode.TemplateMode.TEXT;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@Slf4j
public class ThymeleafConfig {
    public static final int MAX_DEPTH = 12;

    @Configuration
    @Profile("default")
    @RequiredArgsConstructor
    static class GitTemplateResolverConfiguration {
        private final TemplateProperties templateProperties;

        @Value("${templates.git.localRepoPath}")
        private String localRepoPath;
        @Value("${spring.application.name:UNKNOWN}")
        private String applicationName;

        private String local = null;

        @PostConstruct
        void postConstruct() throws IOException {
            log.debug("Using Git.");

            if (localRepoPath.isEmpty()) {
                local = Files.createTempDirectory(applicationName).toFile().getAbsolutePath();
            } else {
                local = localRepoPath;
            }
        }

        @Bean
        ITemplateResolver gitTemplateResolver() throws IOException {
            AbstractConfigurableTemplateResolver templateResolver =
                    new GitTemplateResolver(templateProperties.getBaseUri(), local);
            configureTemplateResolver(templateResolver, local);

            return templateResolver;
        }

        @Bean
        YamlPropertiesFactoryBean gitPropertiesFactory() throws IOException {
            return yamlPropertiesFactory(local, templateProperties.getNames());
        }
    }

    @Configuration
    @Profile("native")
    @RequiredArgsConstructor
    static class FileTemplateResolverConfiguration {
        private final TemplateProperties templateProperties;

        @PostConstruct
        void postConstruct() {
            log.debug("Using FileTemplateResolver.");
        }

        @Bean
        ITemplateResolver fileTemplateResolver() throws IOException {
            AbstractConfigurableTemplateResolver templateResolver = new FileTemplateResolver();
            configureTemplateResolver(templateResolver, templateProperties.getBaseUri());

            return templateResolver;
        }

        @Bean
        YamlPropertiesFactoryBean nativePropertiesFactory() throws IOException {
            return yamlPropertiesFactory(templateProperties.getBaseUri(), templateProperties.getNames());
        }
    }

    static YamlPropertiesFactoryBean yamlPropertiesFactory(String path, List<String> names) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();

        FileSystemResource[] resources = Files.find(Paths.get(path, "properties"),
                MAX_DEPTH,
                (p, attr) -> {
                    String filename = getFilename(cleanPath(p.toFile().getAbsolutePath()));

                    return isYaml(filename) && isGlobalOrMatchesTemplatesNames(filename, names);
                })
                .map(Path::toFile)
                .peek(f -> log.debug("Found resource: {}.", f.getAbsolutePath()))
                .map(FileSystemResource::new)
                .toArray(size -> new FileSystemResource[size]);

        factory.setResources(resources);

        return factory;
    }

    private static boolean isGlobalOrMatchesTemplatesNames(String filename, List<String> names) {
        String stripped = stripFilenameExtension(filename);

        // Keeping profile-specific files in mind
        return filename.startsWith("application")
                || names.stream()
                .filter(stripped::startsWith)
                .findAny()
                .isPresent();
    }

    private static boolean isYaml(String filename) {
        return filename.endsWith(".yaml") || filename.endsWith(".yml");
    }

    static void configureTemplateResolver(AbstractConfigurableTemplateResolver templateResolver, String path) {
        templateResolver.setPrefix(path + "/templates/");
        templateResolver.setSuffix(".template");
        templateResolver.setTemplateMode(TEXT);
        templateResolver.setCharacterEncoding(UTF_8.name());
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(false);
    }

    @Bean
    ITemplateEngine templateEngine(ITemplateResolver templateResolver, YamlPropertiesFactoryBean propertiesFactory,
                                   ConfigurableEnvironment env) {
        Properties props = propertiesFactory.getObject();
        props.putAll(env.getSystemEnvironment());
        props.putAll(env.getSystemProperties());

        StandardMessageResolver messageResolver = new StandardMessageResolver();
        messageResolver.setDefaultMessages(props);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(messageResolver);

        return templateEngine;
    }

    @Bean
    TemplateResolverService templateResolverService(ITemplateEngine templateEngine,
                                                    TemplateProperties templateProperties) {
        return new TemplateResolverService(templateEngine, templateProperties);
    }
}
