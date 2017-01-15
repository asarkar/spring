package name.abhijitsarkar.javaee.salon.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class UserIdAwareUserDetailsDeserializer extends JsonDeserializer<UserDetails> {
	Collection<? extends GrantedAuthority> NO_AUTHORITIES = emptyList();

	@Override
	public UserDetails deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		JsonNode userId = node.path("userId");
		JsonNode username = node.path("username");
		JsonNode password = node.path("password");
		JsonNode authorities = node.path("authorities");

		if (username.isMissingNode() || password.isMissingNode()) {
			throw new IOException("Username and/or password not found.");
		}

		if (userId.isMissingNode()) {
			return new User(username.asText(), password.asText(), NO_AUTHORITIES);
		}

		List<GrantedAuthority> roles = authorities.findValuesAsText("authority").stream()
				.map(SimpleGrantedAuthority::new).collect(toList());

		return new UserIdAwareUserDetails(userId.asLong(), username.asText(), password.asText(),
				unmodifiableList(roles));
	}
}
