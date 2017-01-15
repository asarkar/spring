package name.abhijitsarkar.javaee.salon.appointment;

import java.time.OffsetDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import name.abhijitsarkar.javaee.salon.appointment.service.OffsetDateTimeConverter;
import name.abhijitsarkar.javaee.salon.appointment.service.OffsetDateTimeFormatter;
import name.abhijitsarkar.javaee.salon.domain.OptionalStringConverter;
import name.abhijitsarkar.javaee.salon.web.CatchAllExceptionHandler;

@Configuration
@ComponentScan(basePackageClasses = CatchAllExceptionHandler.class)
@EntityScan(basePackageClasses = { Appointment.class, OptionalStringConverter.class })
public class AppointmentAppConfig {
	// There's also a mvcConversionService
	@Resource(name = "defaultConversionService")
	private FormatterRegistry formatterRegistry;

	@PostConstruct
	void init() throws JsonProcessingException {
		formatterRegistry.removeConvertible(String.class, OffsetDateTime.class);

		formatterRegistry.addFormatter(new OffsetDateTimeFormatter());
		formatterRegistry.addConverter(new OffsetDateTimeConverter());
	}

	@Bean
	ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}

	/*
	 * c.f. JacksonAutoConfiguration, Jackson2ObjectMapperBuilder,
	 * AbstractJackson2HttpMessageConverter, JavaTimeModule
	 */

	@Bean
	public Module jdk8Module() {
		return new Jdk8Module();
	}

	@Bean
	public Module javaTimeModule() {
		return new JavaTimeModule();
	}
}
