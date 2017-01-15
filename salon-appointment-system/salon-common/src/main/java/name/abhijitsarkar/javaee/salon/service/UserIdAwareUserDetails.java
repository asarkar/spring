package name.abhijitsarkar.javaee.salon.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = UserIdAwareUserDetailsDeserializer.class)
public class UserIdAwareUserDetails implements UserDetails {
	private static final long serialVersionUID = 4957038767061529310L;

	private final Long userId;
	private final String username;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public UserIdAwareUserDetails(Long userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {

		this.userId = userId;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
