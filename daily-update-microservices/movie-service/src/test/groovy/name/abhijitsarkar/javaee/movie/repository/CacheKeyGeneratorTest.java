package name.abhijitsarkar.javaee.movie.repository;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * @author Abhijit Sarkar
 */
public class CacheKeyGeneratorTest {
    private CacheKeyGenerator keyGenerator = new CacheKeyGenerator();

    public CacheKeyGeneratorTest() {
        keyGenerator.applicationName = "test";
    }

    @Test
    public void testKeyGenerationForPopularMovies() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(TheMovieDbClient.class, "findPopularMovies"));

        assertEquals(String.format("test.popular.%s", LocalDate.now()), cacheKey);
    }

    @Test
    public void testKeyGenerationForGenres() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(TheMovieDbClient.class, "getAllGenres"));

        assertEquals(String.format("test.genres.%s", LocalDate.now().getMonthValue()), cacheKey);
    }

    @Test
    public void testKeyGenerationForUnknownMethod() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(getClass(), "toString"));

        assertEquals(String.format("test.%s.toString", getClass().getName()),
                cacheKey);
    }
}
