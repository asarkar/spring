package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.write;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class TemplateResolverService {
    @NonNull
    private final ITemplateEngine templateEngine;
    @NonNull
    private final TemplateProperties templateProperties;

    public void resolve() throws IOException {
        Context context = new Context();

        templateProperties.getNames().stream()
                .map(template -> {
                    try {
                        return templateEngine.process(template, context);
                    } catch (RuntimeException e) {
                        log.warn("Failed to process template: {}.", template, e);
                        return "";
                    }
                })
                .filter(result -> !result.isEmpty())
                .findFirst()
                .ifPresent(result -> {
                    String output = templateProperties.getOutput();
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
