package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.write;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class TemplateResolverSvc {
    @Value("${template.output:console}")
    private String output;

    @Value("${template.names:UNKNOWN}")
    private List<String> names;

    private final ITemplateEngine templateEngine;
    private final Map<String, Object> properties;

    public void resolve() throws IOException {
        Path p = null;

        if (!output.equalsIgnoreCase("console")) {
            p = Paths.get(output);
            if (!isWritable(p)) {
                throw new IllegalArgumentException("Cannot write to: " + p);
            }
        }

        Context context = new Context();
        context.setVariables(properties);
        boolean success = false;

        for (String template : names) {
            try {
                String result = templateEngine.process(template, context);
                if (p == null) {
                    System.out.println(result);
                } else {
                    write(p, result.getBytes(UTF_8));
                }
                success = true;
                break;
            } catch (TemplateProcessingException e) {
                log.warn("Failed to process template: {}.", template);
                success |= false;
            }
        }

        if (!success) {
            throw new TemplateProcessingException(String.format("None of the following templates could be resolved: %s",
                    names));
        }
    }
}
