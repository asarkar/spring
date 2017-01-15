package name.abhijitsarkar.javaee.salon.common;

import static com.google.common.collect.Lists.newArrayList;
import static name.abhijitsarkar.javaee.salon.domain.Role.ROLE_ANONYMOUS;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;
import name.abhijitsarkar.javaee.salon.service.UserIdAwareUserDetails;

public class UserIdAwareUserDetailsTest {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	private static final Collection<? extends GrantedAuthority> AUTHORITIES = newArrayList(
			new SimpleGrantedAuthority(ROLE_ANONYMOUS.name()));

	private UserIdAwareUserDetails userDetails = new UserIdAwareUserDetails(1l, "test", "test", AUTHORITIES);

	@Test
	public void testSerialization() throws JsonProcessingException {
		OBJECT_MAPPER.writeValueAsString(userDetails);
	}

	@Test
	public void testDeserialization() throws IOException {
		UserDetails userDetails = OBJECT_MAPPER.readValue(getClass().getResourceAsStream("/user-details.json"),
				UserIdAwareUserDetails.class);
	}
}
