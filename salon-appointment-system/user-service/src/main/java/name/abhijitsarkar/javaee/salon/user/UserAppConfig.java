package name.abhijitsarkar.javaee.salon.user;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import name.abhijitsarkar.javaee.salon.domain.OptionalStringConverter;
import name.abhijitsarkar.javaee.salon.user.domain.User;
import name.abhijitsarkar.javaee.salon.web.CatchAllExceptionHandler;

@Configuration
@ComponentScan(basePackageClasses = CatchAllExceptionHandler.class)
@EntityScan(basePackageClasses = { User.class, OptionalStringConverter.class })
public class UserAppConfig {
	@Bean
	ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}

	@Bean
	public Module jdk8Module() {
		return new Jdk8Module();
	}
}
