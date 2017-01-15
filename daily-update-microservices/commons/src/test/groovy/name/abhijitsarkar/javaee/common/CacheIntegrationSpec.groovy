package name.abhijitsarkar.javaee.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Abhijit Sarkar
 */
@ContextConfiguration(classes = [IntegrationTestConfig, CommonConfig])
class CacheIntegrationSpec extends Specification {
    @Autowired
    RandomNumberGenerator generator

    def "caches random number"() {
        setup:
        Integer random = generator.random(10);

        expect:
        (1..10).every { random == generator.random(10) }
    }
}
