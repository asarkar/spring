package name.abhijitsarkar.javaee.salon.user.domain;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PhoneNumberConverter implements AttributeConverter<String, String> {
	@Override
	public String convertToDatabaseColumn(String phoneNum) {
		// Allow '%' for LIKE queries
		return phoneNum.chars().mapToObj(i -> Character.toString((char) i)).filter(s -> s.matches("[%\\d]"))
				.collect(joining());
	}

	@Override
	public String convertToEntityAttribute(String phoneNum) {
		// In case someone had updated in the DB directly
		checkState(phoneNum.length() == 10, "Phone number must have exactly 10 digits. %s", phoneNum);

		// The offset is the index *after* which the object is inserted
		return new StringBuilder(phoneNum).insert(3, '-').insert(7, '-').toString();
	}
}
