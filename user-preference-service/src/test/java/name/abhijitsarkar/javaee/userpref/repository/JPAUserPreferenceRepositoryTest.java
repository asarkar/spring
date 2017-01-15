package name.abhijitsarkar.javaee.userpref.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import name.abhijitsarkar.javaee.userpref.TestUserPreferenceApp;
import name.abhijitsarkar.javaee.userpref.domain.UserPreferenceEntity;
import name.abhijitsarkar.javaee.userpref.domain.UserPreferenceId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestUserPreferenceApp.class)
@WebAppConfiguration
@Transactional
public class JPAUserPreferenceRepositoryTest {
    @Autowired
    private JPAUserPreferenceRepository userPreferenceRepository;

    private static final UserPreferenceId NON_EXISTENT_USER_PREFERENCE;

    static {
	NON_EXISTENT_USER_PREFERENCE = new UserPreferenceId("junk", -1, "who");
    }

    @Test
    public void testCreateUserPreference() {
	assertTrue(createUserPreference().isPresent());
    }

    private Optional<UserPreferenceEntity> createUserPreference() {
	// create some random preference name for an existing service and user 
	int randomPreference = new Random().nextInt(25);

	UserPreferenceId id = new UserPreferenceId(
		Integer.toString(randomPreference++), 3, "johndoe");
	UserPreferenceEntity userPreference = new UserPreferenceEntity(id,
		Integer.toString(randomPreference));

	return userPreferenceRepository.save(userPreference);
    }

    @Test
    public void testFindUserPreference() {
	Optional<UserPreferenceEntity> newUserPreference = createUserPreference();

	UserPreferenceId newId = newUserPreference.map(pref -> pref.getId())
		.orElse(NON_EXISTENT_USER_PREFERENCE);

	UserPreferenceId foundId = findUserPreferenceById(newId);

	assertNotEquals(NON_EXISTENT_USER_PREFERENCE, foundId);
	assertEquals(newId, foundId);
    }

    private UserPreferenceId findUserPreferenceById(UserPreferenceId id) {
	Optional<UserPreferenceEntity> userPreference = userPreferenceRepository
		.findOne(id);

	return userPreference.map(pref -> pref.getId())
		.orElse(NON_EXISTENT_USER_PREFERENCE);
    }

    @Test
    public void testDeleteUserPreference() {
	Optional<UserPreferenceEntity> newUserPreference = createUserPreference();
	assertTrue(newUserPreference.isPresent());

	UserPreferenceId newId = newUserPreference.get().getId();

	userPreferenceRepository.delete(newId);

	UserPreferenceId foundId = findUserPreferenceById(newId);

	assertEquals(NON_EXISTENT_USER_PREFERENCE, foundId);
	assertNotEquals(newId, foundId);
    }
}
