package name.abhijitsarkar.javaee.auth.service;

import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Abhijit Sarkar
 */
@RunWith(value = Parameterized.class)
public class FileSystemUserDetailsServiceTest {
    private static final String PASSWORD = "secret";

    private String username;
    private String authority;
    private boolean enabled;

    private FileSystemUserDetailsService userDetailsService = new FileSystemUserDetailsService();

    @Parameterized.Parameters(name = "username: {0}")
    public static Collection data() {
        return asList(new Object[][]{
                {"abhijitsarkar", true, "ADMIN"},
                {"johndoe", true, "NEWS"},
                {"janedoe", true, "MOVIES"},
                {"johnnyappleseed", true, "WEATHER"},
                {"nopayer", false, ""}
        });
    }

    public FileSystemUserDetailsServiceTest(String username, boolean enabled, String authority) throws IOException {
        this.username = username;
        this.enabled = enabled;
        this.authority = authority;

        userDetailsService.objectMapper = ObjectMapperFactory.newInstance();

        userDetailsService.init();
    }

    @Test
    public void testValidUsers() throws Exception {
        Arrays.stream(new String[]{"abhijitsarkar", "johndoe", "janedoe", "johnnyappleseed"})
                .forEach(username -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    assertEquals(PASSWORD, userDetails.getPassword());
                    assertTrue(userDetails.isEnabled());
                });
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testNonExistingUser() {
        userDetailsService.loadUserByUsername("noone");
    }

    @Test
    public void testLoadUserByUsername() {
        UserDetails user = userDetailsService.loadUserByUsername(username);

        assertEquals(username, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(enabled, user.isEnabled());

        assertTrue(user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(","))
                .equals(authority));
    }
}