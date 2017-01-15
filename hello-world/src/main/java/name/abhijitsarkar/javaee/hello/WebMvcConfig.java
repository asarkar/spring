package name.abhijitsarkar.javaee.hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Abhijit Sarkar
 */
@Configuration
/* Use @EnableWebMvc for complete control which turns off WebMvcAutoConfiguration */
public class WebMvcConfig extends WebMvcConfigurerAdapter {
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
