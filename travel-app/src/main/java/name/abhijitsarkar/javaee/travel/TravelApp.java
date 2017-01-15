package name.abhijitsarkar.javaee.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
public class TravelApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(TravelApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TravelApp.class);
    }
}
