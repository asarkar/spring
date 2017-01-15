package name.abhijitsarkar.javaee.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Abhijit Sarkar
 */
@Configuration
class IntegrationTestConfig {
    @Bean
    RandomNumberGenerator randomNumberGenerator() {
        new RandomNumberGenerator()
    }
}
