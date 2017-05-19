package org.abhijitsarkar.coolhttpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Abhijit Sarkar
 */
public class JokeServiceResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    public JokeServiceResponseTest() {
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    public void testUnmarshal() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/joke.json")) {
            JokeServiceResponse response = mapper.readValue(is, JokeServiceResponse.class);

            assertThat(response)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("status", "success");
            assertThat(response.getJoke())
                    .isNotEqualTo(Optional.empty());
            assertThat(response.getJoke().get())
                    .hasFieldOrPropertyWithValue("categories", emptyList())
                    .hasFieldOrPropertyWithValue("id", 84)
                    .extracting("text")
                    .isNotEmpty();
        }
    }
}