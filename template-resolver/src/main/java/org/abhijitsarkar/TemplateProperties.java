package org.abhijitsarkar;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("template")
@Component
public class TemplateProperties {
    @Setter
    private String baseUri;
    @Setter
    private String output;
    private List<String> names;

    public String getBaseUri() {
        return baseUri;
    }

    public String getOutput() {
        return output == null ? "console" : output;
    }

    public List<String> getNames() {
        return isEmpty(names) ? singletonList("UNKNOWN") : names;
    }

    public void setNames(String names) {
        this.names = Arrays.stream(names.split(","))
                .map(String::trim)
                .collect(toList());
    }
}
