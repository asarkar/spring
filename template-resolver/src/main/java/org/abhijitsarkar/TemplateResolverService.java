package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.write;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class TemplateResolverService {
    @Value("${template.output:console}")
    String output;

    @Value("${template.names:UNKNOWN}")
    String names;

    private final ITemplateEngine templateEngine;
    private final Map<String, Object> properties;

    public void resolve() throws IOException {
        Context context = new Context();
        context.setVariables(properties);

        Arrays.stream(names.split(","))
                .map(String::trim)
                .map(template -> {
                    try {
                        return templateEngine.process(template, context);
                    } catch (RuntimeException e) {
                        log.warn("Failed to process template: {}.", template);
                        return "";
                    }
                })
                .filter(result -> !result.isEmpty())
                .findFirst()
                .ifPresent(result -> {
                    if (output.equalsIgnoreCase("console")) {
                        System.out.println(result);
                    } else {
                        Path p = Paths.get(output);
                        try {
                            deleteIfExists(p);
                            write(p, result.getBytes(UTF_8));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                });
    }
}
