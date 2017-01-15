package name.abhijitsarkar.javaee.userpref.repository;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.abhijitsarkar.javaee.userpref.domain.SecurityContext;
import name.abhijitsarkar.javaee.userpref.domain.UserPreference;
import name.abhijitsarkar.javaee.userpref.domain.UserPreferenceEntity;
import name.abhijitsarkar.javaee.userpref.domain.UserPreferenceId;

@Repository
@Profile("!oracle")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
// SimpleJpaRepository throws an exception if entity is not found
public class DelegatingUserPreferenceRepository
	implements UserPreferenceRepository {
    private final JPAUserPreferenceRepository userPreferenceRepository;
    private final SecurityContext securityContext;

    @Override
    public Optional<UserPreference> findOne(UserPreference userPreference) {
	UserPreferenceId id = new UserPreferenceId(userPreference.getName(),
		userPreference.getServiceId(), securityContext.getUsername());

	Optional<UserPreferenceEntity> foundUserPreference = null;

	try {
	    foundUserPreference = userPreferenceRepository.findOne(id);
	} catch (NullPointerException npe) {
	    foundUserPreference = Optional.empty();
	}

	log.info("Retrieved user preference: {}.", foundUserPreference);

	return foundUserPreference.map(ENTITY_TO_USER_PREFERENCE);
    }

    @Override
    public Optional<UserPreference> save(UserPreference userPreference) {
	UserPreferenceId id = new UserPreferenceId(userPreference.getName(),
		userPreference.getServiceId(), securityContext.getUsername());

	Optional<UserPreferenceEntity> savedUserPreference = null;

	try {
	    savedUserPreference = userPreferenceRepository.save(
		    new UserPreferenceEntity(id, userPreference.getValue()));
	} catch (NullPointerException npe) {
	    savedUserPreference = Optional.empty();
	}

	log.info("Saved user preference: {}.", savedUserPreference);

	return savedUserPreference.map(ENTITY_TO_USER_PREFERENCE);

    }

    @Override
    public Optional<UserPreference> delete(UserPreference userPreference) {
	UserPreferenceId id = new UserPreferenceId(userPreference.getName(),
		userPreference.getServiceId(), securityContext.getUsername());

	// TODO: What happens if id is not found?

	Optional<UserPreference> deletedUserPreference = null;

	try {
	    userPreferenceRepository.delete(id);
	    deletedUserPreference = Optional.of(userPreference);
	} catch (NullPointerException npe) {
	    deletedUserPreference = Optional.empty();
	}

	log.info("Deleted user preference for id: {}.", id);

	return deletedUserPreference;
    }

    private static final Function<UserPreferenceEntity, UserPreference> ENTITY_TO_USER_PREFERENCE = new Function<UserPreferenceEntity, UserPreference>() {
	@Override
	public UserPreference apply(UserPreferenceEntity entity) {
	    UserPreferenceId id = entity.getId();
	    return new UserPreference(id.getName(), entity.getValue(),
		    id.getServiceId());
	}
    };
}
