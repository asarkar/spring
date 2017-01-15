package name.abhijitsarkar.javaee.salon.appointment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import name.abhijitsarkar.javaee.salon.service.UserIdAwareUserDetails;

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
public class UserIdAwareAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserIdAwareAuthenticationProvider.class);

	private final UserDetailsService userDetailsService;

	public UserIdAwareAuthenticationProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (userDetails instanceof UserIdAwareUserDetails) {
			LOGGER.info("Setting user id in user details.");

			authentication.setDetails(((UserIdAwareUserDetails) userDetails).getUserId());
		}

		return userDetails;
	}
}
