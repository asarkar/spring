package name.abhijitsarkar.javaee.userpref.repository;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import name.abhijitsarkar.javaee.userpref.domain.SecurityContext;
import name.abhijitsarkar.javaee.userpref.domain.UserPreference;

@Repository
@Profile("oracle")
public class UserPreferenceServiceimpl implements UserPreferenceRepository {
    @Resource(name = "oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SecurityContext securityContext;

    @Override
    public Optional<UserPreference> findOne(UserPreference userPreference) {
	Object result = new GetUserPreferenceSproc(jdbcTemplate).execute(
		securityContext.getUsername(), null,
		userPreference.getServiceId(), securityContext.getPartnerId(),
		userPreference.getName());

	UserPreference pref = result == null ? null : (UserPreference) result;

	return Optional.ofNullable(pref);
    }
}
