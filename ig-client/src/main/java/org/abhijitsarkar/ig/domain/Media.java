package org.abhijitsarkar.ig.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

/**
 * @author Abhijit Sarkar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Media {
    @JsonProperty("data")
    private Collection<Medium> media;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Medium {
        private String link;
        private Likes likes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Likes {
        private long count;
    }
}

