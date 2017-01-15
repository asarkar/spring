package name.abhijitsarkar.javaee.movie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Abhijit Sarkar
 */
public class GenresTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

    @Test
    public void testDeserialization() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/genres.json")) {
            ObjectReader reader = objectMapper.reader();

            List<Integer> genres = reader.forType(Genres.class).<Genres>readValue(is).
                    getGenres().stream().map(Genre::getId).collect(toList());

            assertFalse(genres.isEmpty());

            assertTrue(IntStream.of(28, 80, 53).allMatch(genres::contains));
        }
    }
}
