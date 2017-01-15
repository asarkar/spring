package name.abhijitsarkar.javaee.salon.user.repository;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static name.abhijitsarkar.javaee.salon.domain.Role.ROLE_ANONYMOUS;
import static name.abhijitsarkar.javaee.salon.domain.Role.ROLE_USER;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.google.common.collect.ImmutableList;

import name.abhijitsarkar.javaee.salon.service.UserIdAwareUserDetails;
import name.abhijitsarkar.javaee.salon.user.domain.Authority;
import name.abhijitsarkar.javaee.salon.user.domain.User;

public class AuthorityRepositoryImpl implements AuthorityRepositoryCustom, UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityRepositoryImpl.class);

	private static final String SELECT_AUTHORITIES = "SELECT a FROM Authority a WHERE a.user.email = ?1";
	private static final String SELECT_USER = "SELECT u FROM User u WHERE u.email = ?1";

	@Autowired
	private EntityManager em;

	@Override
	public UserDetails loadUserByUsername(String username) {
		TypedQuery<Authority> q1 = em.createQuery(SELECT_AUTHORITIES, Authority.class);
		q1.setParameter(1, Optional.of(username));

		List<Authority> authorities = q1.getResultList();

		Long userId = -1l;

		Collection<? extends GrantedAuthority> roles = null;

		if (!authorities.isEmpty()) {
			userId = authorities.get(0).getUser().getId();

			roles = unmodifiableList(authorities.stream().map(a -> a.getRole().name()).map(SimpleGrantedAuthority::new)
					.collect(toList()));
		} else {
			TypedQuery<User> q2 = em.createQuery(SELECT_USER, User.class);
			q2.setParameter(1, Optional.of(username));

			try {
				userId = q2.getSingleResult().getId();

				LOGGER.warn("User: {} has no authorities. Assigning authority: {} for current request.", username,
						ROLE_USER);

				roles = ImmutableList.of(new SimpleGrantedAuthority(ROLE_USER.name()));
			} catch (PersistenceException e) {
				LOGGER.warn("User: {} lookup failed.", e.getMessage());

				roles = ImmutableList.of(new SimpleGrantedAuthority(ROLE_ANONYMOUS.name()));
			}
		}

		return new UserIdAwareUserDetails(userId, username, "secret", roles);
	}
}
