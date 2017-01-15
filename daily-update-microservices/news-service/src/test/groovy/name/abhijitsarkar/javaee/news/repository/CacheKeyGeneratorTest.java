package name.abhijitsarkar.javaee.news.repository;

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
    public void testKeyGenerationForTopStoriesInWorld() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(NYTClient.class, "getTopStories", String.class), "world");

        assertEquals(String.format("test.top.world.%s", LocalDate.now()), cacheKey);
    }

    @Test
    public void testKeyGenerationForUnknownMethod() {
        Object cacheKey = keyGenerator.generate(
                this, findMethod(getClass(), "toString"));

        assertEquals(String.format("test.%s.toString", getClass().getName()),
                cacheKey);
    }
}
