package name.abhijitsarkar.javaee.news.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

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

        if (methodName.toLowerCase().contains("top")) {
            return String.format("%s.top.%s.%s", applicationName,
                    Arrays.stream(params).
                            map(Object::toString).
                            collect(joining(",")),
                    LocalDate.now());
        }

        log.warn("Unknown method name. Generating default key for caching.");

        return String.format("%s.%s.%s", applicationName, target.getClass().getName(), method.getName());
    }
}
