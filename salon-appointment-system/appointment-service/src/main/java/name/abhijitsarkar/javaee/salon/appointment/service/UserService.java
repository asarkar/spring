package name.abhijitsarkar.javaee.salon.appointment.service;

import java.util.Collection;

public interface UserService {
	// boolean isValidUser(long userId);

	Collection<Long> getUserIdsByFirstName(String firstName);

	Collection<Long> getUserIdsByLastName(String lastName);

	Collection<Long> getUserIdsByFirstAndLastNames(String firstName, String lastName);

	Collection<Long> getUserIdsByEmail(String email);

	Collection<Long> getUserIdsByPhoneNum(String phoneNum);

	Collection<Long> getUserIdsByPhoneNumEndingWith(String phoneNum);
}
