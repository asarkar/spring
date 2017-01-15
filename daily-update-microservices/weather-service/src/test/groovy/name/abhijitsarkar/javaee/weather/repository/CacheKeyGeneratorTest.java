package name.abhijitsarkar.javaee.weather.repository;

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
                this,
                findMethod(OpenWeatherMapClient.class, "getWeatherByZipCodeAndCountry", int.class, String.class),
                98106, "imperial");

        assertEquals(String.format("test.zipcode.98106,imperial.%s", LocalDate.now()), cacheKey);
    }

    @Test
    public void testKeyGenerationForUnknownMethod() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(getClass(), "toString"));

        assertEquals(String.format("test.%s.toString", getClass().getName()),
                cacheKey);
    }
}
