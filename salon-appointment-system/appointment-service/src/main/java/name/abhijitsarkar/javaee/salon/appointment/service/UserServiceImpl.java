package name.abhijitsarkar.javaee.salon.appointment.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

import java.util.Base64;
import java.util.Collection;

import name.abhijitsarkar.javaee.salon.appointment.domain.User;
import name.abhijitsarkar.javaee.salon.appointment.domain.UserSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import name.abhijitsarkar.javaee.salon.service.UserIdAwareUserDetails;

/* Good SPEL examples: http://dhruba.name/2009/12/30/spring-expression-language-spel-primer/ */
@Service
@ConditionalOnExpression("#{(environment['spring.profiles.active']?:'').split(',').?[#this == 'NoReg' or #this == 'NoAuth'].length == 0}")
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	private RestTemplate restTemplate;

	@Value("${user-service.url}")
	private String userServiceUrl;

	private final HttpEntity<Void> dummyEntity;

	public UserServiceImpl() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(asList(HAL_JSON));

		dummyEntity = new HttpEntity<Void>(null, headers);
	}

	// @Override
	// public boolean isValidUser(long userId) {
	// try {
	// restTemplate.headForHeaders(userServiceUrl + "/users/{userId}", userId);
	//
	// return true;
	// } catch (RestClientException e) {
	// LOGGER.warn("User look up by user id: {} failed.", userId, e);
	//
	// return false;
	// }
	// }

	@Override
	public Collection<Long> getUserIdsByFirstName(String firstName) {
		String findByFirstNameUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findByFirstName").queryParam("firstName", firstName).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByFirstNameUri, GET, newEntity(),
				UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public Collection<Long> getUserIdsByLastName(String lastName) {
		String findByLastNameUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findByLastName").queryParam("lastName", lastName).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByLastNameUri, GET, newEntity(),
				UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public Collection<Long> getUserIdsByFirstAndLastNames(String firstName, String lastName) {
		String findByFirstAndLastNamesUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findByFirstNameAndLastName").queryParam("firstName", firstName)
				.queryParam("lastName", lastName).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByFirstAndLastNamesUri, GET,
				newEntity(), UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public Collection<Long> getUserIdsByEmail(String email) {
		String findByEmailUri = UriComponentsBuilder.fromUriString(userServiceUrl).path("/users/search/findByEmail")
				.queryParam("email", email).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByEmailUri, GET, newEntity(),
				UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public Collection<Long> getUserIdsByPhoneNum(String phoneNum) {
		String findByPhoneNumUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findByPhoneNum").queryParam("phoneNum", phoneNum).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByPhoneNumUri, GET, newEntity(),
				UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public Collection<Long> getUserIdsByPhoneNumEndingWith(String phoneNum) {
		String findByPhoneNumEndingWithUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findByPhoneNumEndingWith").queryParam("phoneNum", phoneNum).toUriString();

		ResponseEntity<UserSearchResult> userSearchResult = restTemplate.exchange(findByPhoneNumEndingWithUri, GET,
				newEntity(), UserSearchResult.class);

		return convertToIds(userSearchResult);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String findUserDetailsByUsernameUri = UriComponentsBuilder.fromUriString(userServiceUrl)
				.path("/users/search/findUserDetailsByUsername").queryParam("username", username).toUriString();

		ResponseEntity<? extends UserDetails> userDetails = restTemplate.exchange(findUserDetailsByUsernameUri, GET,
				dummyEntity, UserIdAwareUserDetails.class);

		return userDetails.getBody();
	}

	private Collection<Long> convertToIds(ResponseEntity<UserSearchResult> userSearchResult) {
		if (userSearchResult.getStatusCode().is2xxSuccessful()) {
			return userSearchResult.getBody().getEmbedded().getUsers().stream().map(User::getUserId).collect(toList());
		}

		return emptyList();
	}

	private HttpEntity<Void> newEntity() {
		Object authObj = SecurityContextHolder.getContext().getAuthentication();

		if (authObj == null) {
			throw new AuthenticationCredentialsNotFoundException(
					"Authentication object not found in the SecurityContext or session.");
		}

		Object principal = ((Authentication) authObj).getPrincipal();

		if (!(principal instanceof UserDetails)) {
			throw new UsernameNotFoundException("Expected principal to be a UserDetails.");
		}

		UserDetails userDetails = ((UserDetails) principal);

		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, newBasicAuthHeader(userDetails));
		headers.setAccept(asList(HAL_JSON));

		return new HttpEntity<Void>(null, headers);
	}

	private String newBasicAuthHeader(UserDetails userDetails) {
		String auth = String.format("%s:%s", userDetails.getUsername(), userDetails.getPassword());
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(UTF_8));

		return String.format("Basic %s", encodedAuth);
	}
}
