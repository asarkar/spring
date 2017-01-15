package name.abhijitsarkar.javaee.salon.appointment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import name.abhijitsarkar.javaee.salon.appointment.service.AppointmentPermissionEvaluator;
import name.abhijitsarkar.javaee.salon.appointment.service.PageAwareMethodSecurityExpressionHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!NoAuth")
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	// http://stackoverflow.com/questions/23638462/how-do-i-add-method-based-security-to-a-spring-boot-project
	@Bean
	PermissionEvaluator appointmentPermissionEvaluator() {
		return new AppointmentPermissionEvaluator();
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		PageAwareMethodSecurityExpressionHandler handler = new PageAwareMethodSecurityExpressionHandler();

		handler.setPermissionEvaluator(appointmentPermissionEvaluator());

		return handler;
	}
}
