package name.abhijitsarkar.javaee.common

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

/**
 * @author Abhijit Sarkar
 */
@CacheConfig(cacheResolver = "cacheResolver")
class RandomNumberGenerator {
    private final Random randomNumGenerator = new Random()

    @Cacheable
    Integer random(int bound) {
        randomNumGenerator.nextInt(bound)
    }
}
