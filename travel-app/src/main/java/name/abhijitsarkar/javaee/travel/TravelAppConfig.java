package name.abhijitsarkar.javaee.travel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.CREATOR;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.SETTER;

/**
 * @author Abhijit Sarkar
 */
@Configuration
public class TravelAppConfig extends WebMvcConfigurerAdapter {
    @Bean
    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
                .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

//        mapper.setPropertyNamingStrategy(
//                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        mapper.setVisibility(FIELD, ANY)
                .setVisibility(GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(SETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(CREATOR, JsonAutoDetect.Visibility.NONE);

        return mapper;
    }

    /* Don't map URLs with suffixes to handlers. Give a chance to ResourceHttpRequestHandler.
    * This is usually not necessary if static resources don't map to URLs for which, if the suffix is stripped off,
    * some handler exists. For example, if /abc maps to a handler and /abc.html to a static page, with suffix
    * pattern matching enabled, the request maps to the handler, not to the ResourceHttpRequestHandler.
    *
    * c.f. WebMvcProperties.staticPathPattern and ResourceProperties for more configuration options.
    */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        super.configurePathMatch(configurer);

        configurer.setUseSuffixPatternMatch(false);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}
