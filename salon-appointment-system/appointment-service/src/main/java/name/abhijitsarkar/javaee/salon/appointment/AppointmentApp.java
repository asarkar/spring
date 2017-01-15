package name.abhijitsarkar.javaee.salon.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableDiscoveryClient
@Profile("!NoReg")
public class AppointmentApp {
	public static void main(String[] args) {
		System.setProperty("spring.config.location", "classpath:/appointment-service.yml");
		System.setProperty("spring.cloud.bootstrap.location", "classpath:/service-registration.yml");

		SpringApplication.run(AppointmentApp.class, args);
	}
}
