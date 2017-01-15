package name.abhijitsarkar.javaee.movie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import name.abhijitsarkar.javaee.common.domain.Movie;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Abhijit Sarkar
 */
public class MoviesTest {
    private ObjectMapper mapper = ObjectMapperFactory.newInstance();

    @Test
    public void testDeserialization() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/popular-movies.json")) {
            Movies result = mapper.readValue(is, Movies.class);

            Collection<Movie> movies = result.getMovies();

            assertNotNull(movies);

            assertFalse(movies.isEmpty());

            assertTrue(movies.stream().anyMatch(m -> m.getTitle().toLowerCase().contains("star wars")));
        }
    }
}
