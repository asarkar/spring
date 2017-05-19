package org.abhijitsarkar.coolhttpclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Abhijit Sarkar
 */
@Getter
public class JokeServiceResponse {
    private final String status;
    private final Optional<Joke> joke;

    public JokeServiceResponse() {
        this("failure");
    }

    public JokeServiceResponse(String status) {
        this(status, empty());
    }

    @JsonCreator
    public JokeServiceResponse(@JsonProperty("type") String status, @JsonProperty("value") Optional<Joke> joke) {
        this.status = status;
        this.joke = joke;
    }

    @Getter
    static class Joke {
        private final int id;
        private final String text;
        private final Collection<String> categories;

        @JsonCreator
        public Joke(@JsonProperty("id") int id, @JsonProperty("joke") String text,
                    @JsonProperty("categories") Collection<String> categories) {
            this.id = id;
            this.text = text;
            this.categories = isEmpty(categories) ? emptyList() : categories;
        }
    }
}
