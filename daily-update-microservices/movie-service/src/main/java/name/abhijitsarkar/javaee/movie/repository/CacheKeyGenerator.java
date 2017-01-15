package name.abhijitsarkar.javaee.movie.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;

/**
 * @author Abhijit Sarkar
 */
@Component
@Slf4j
public class CacheKeyGenerator implements KeyGenerator {
    @Value("${spring.application.name}")
    String applicationName;

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String methodName = method.getName();

        if (methodName.toLowerCase().contains("popular")) {
            return String.format("%s.popular.%s", applicationName, LocalDate.now());
        } else if (methodName.toLowerCase().contains("genre")) {
            return String.format("%s.genres.%s", applicationName, LocalDate.now().getMonthValue());
        }

        log.warn("Unknown method name. Generating default key for caching.");

        return String.format("%s.%s.%s", applicationName, target.getClass().getName(), method.getName());
    }
}
