package name.abhijitsarkar.javaee.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.type.MapType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Abhijit Sarkar
 */
@Service
public class FileSystemUserDetailsService implements UserDetailsService {
    @Autowired
    ObjectMapper objectMapper;

    private Map<String, UserDetails> userDetailsMap;

    @PostConstruct
    void init() throws IOException {
        Assert.state(objectMapper != null, "ObjectMapper must not be null.");

        try (InputStream usersStream = getClass().getResourceAsStream("/users.json");
             InputStream authoritiesStream = getClass().getResourceAsStream("/authorities.json")) {
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                    HashMap.class, String.class, JsonNode.class);

            Map<String, JsonNode> userMap = objectMapper.readValue(usersStream, mapType);
            Map<String, JsonNode> authorityMap = objectMapper.readValue(authoritiesStream, mapType);

            Map<String, UserDetails> tmp = userMap.keySet().stream().collect(toMap(identity(), username -> {
                JsonNode user = userMap.get(username);

                String password = user.path("password").asText();
                boolean enabled = user.path("enabled").asBoolean();
                String strRoles = authorityMap.getOrDefault(username, MissingNode.getInstance())
                        .asText();

                Collection<? extends GrantedAuthority> roles =
                        Arrays.stream(strRoles.split("\\s*?,\\s*?"))
                                .map(String::trim)
                                .filter(role -> !role.isEmpty())
                                .map(SimpleGrantedAuthority::new)
                                .collect(toList());

                return new User(username, password, enabled, true, true, true, roles);
            }));

            userDetailsMap = unmodifiableMap(tmp);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.state(userDetailsMap != null, "userDetailsMap must not be null.");

        UserDetails user = userDetailsMap.get(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No such user: %s.", username));
        }

        // Since we're using an in-memory map, when Spring erases the credentials, it
        // also gets erased from the backing map. Return a copy each time to prevent that.
        return new User(username, user.getPassword(), user.isEnabled(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), user.getAuthorities());
    }
}
