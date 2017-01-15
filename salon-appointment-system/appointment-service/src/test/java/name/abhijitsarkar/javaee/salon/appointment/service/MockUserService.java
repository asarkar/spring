package name.abhijitsarkar.javaee.salon.appointment.service;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockUserService implements UserService {
	private static final Collection<Long> MOCK_USER_IDS = asList(1L);

	// @Override
	// public boolean isValidUser(long userId) {
	// return true;
	// }

	@Override
	public Collection<Long> getUserIdsByFirstName(String firstName) {
		return MOCK_USER_IDS;
	}

	@Override
	public Collection<Long> getUserIdsByLastName(String lastName) {
		return MOCK_USER_IDS;
	}

	@Override
	public Collection<Long> getUserIdsByFirstAndLastNames(String firstName, String lastName) {
		return MOCK_USER_IDS;
	}

	@Override
	public Collection<Long> getUserIdsByEmail(String email) {
		return MOCK_USER_IDS;
	}

	@Override
	public Collection<Long> getUserIdsByPhoneNum(String phoneNum) {
		return MOCK_USER_IDS;
	}

	@Override
	public Collection<Long> getUserIdsByPhoneNumEndingWith(String phoneNum) {
		return MOCK_USER_IDS;
	}
}
