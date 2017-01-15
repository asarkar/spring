package name.abhijitsarkar.javaee.salon.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = RepositoryRestMvcAutoConfiguration.class)
@EnableEurekaServer
public class RegistrationServer {
	public static void main(String[] args) {
		// Tell Boot to look for registration-server.yml
		System.setProperty("spring.config.name", "registration-server");
		SpringApplication.run(RegistrationServer.class, args);
	}
}
