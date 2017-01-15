package name.abhijitsarkar.javaee.salon.appointment;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import name.abhijitsarkar.javaee.salon.appointment.service.UserIdAwareAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!NoAuth")
public class AppointmentAppSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String[] SECURE_PATTERNS = { "/appointments", "/appointments/**/*" };
	private static final String[] WILDCARD_PATTERN = { "/**" };

	@Autowired
	private UserDetailsService userDetailsService;

	/*
	 * GOTCHA ALERT: Probably because UserIdAwareAuthenticationProvider
	 * subclasses AbstractUserDetailsAuthenticationProvider, Spring Security
	 * picks it up as a bean. Explicitly declaring it a bean causes
	 * DaoAuthenticationProvider to run after userIdAwareAuthenticationProvider,
	 * and fail authentication. I've not investigated deeper but an educated
	 * guess looks like all subclasses of
	 * AbstractUserDetailsAuthenticationProvider (meaning
	 * allAuthenticationProvider) are picked up.
	 */
	AuthenticationProvider userIdAwareAuthenticationProvider() {
		return new UserIdAwareAuthenticationProvider(userDetailsService);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web //
				.ignoring() //
				.antMatchers("/console/*") // allow H2 console
				.antMatchers(HEAD, WILDCARD_PATTERN); //
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http // don't format, stupid
				.authorizeRequests() //
				.antMatchers(GET, SECURE_PATTERNS).authenticated() //
				.antMatchers(POST).hasAnyRole("ADMIN") //
				.antMatchers(DELETE).hasAnyRole("ADMIN") //
				.antMatchers(PUT).hasAnyRole("ADMIN") //
				// the last one needs a URL, else throws exception
				.antMatchers(PATCH, WILDCARD_PATTERN).hasAnyRole("ADMIN").and() //
				.httpBasic() //
				.and().csrf().disable() //
				.headers().frameOptions().disable() // allow H2 console
				.authenticationProvider(userIdAwareAuthenticationProvider()); //
	}
}
