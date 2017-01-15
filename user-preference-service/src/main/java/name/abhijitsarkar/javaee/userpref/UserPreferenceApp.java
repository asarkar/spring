package name.abhijitsarkar.javaee.userpref;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

@SpringBootApplication
public class UserPreferenceApp extends SpringBootServletInitializer {
    public static void main(String[] args) throws Exception {
	SpringApplication.run(UserPreferenceApp.class, args);
    }

    @Bean
    ServletRegistrationBean h2servletRegistration() {
	return new ServletRegistrationBean(new WebServlet(), "/console/*");
    }

    @Bean
    View error() {
	return new InternalResourceView("/bad.html");
    }

    @Override
    protected SpringApplicationBuilder configure(
	    SpringApplicationBuilder application) {
	return application.sources(UserPreferenceApp.class);
    }
}
