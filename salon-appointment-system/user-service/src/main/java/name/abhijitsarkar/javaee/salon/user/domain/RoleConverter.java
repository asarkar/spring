package name.abhijitsarkar.javaee.salon.user.domain;

import static name.abhijitsarkar.javaee.salon.domain.Role.ROLE_ANONYMOUS;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.abhijitsarkar.javaee.salon.domain.Role;

@Converter
public class RoleConverter implements AttributeConverter<Role, String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleConverter.class);

	@Override
	public String convertToDatabaseColumn(Role role) {
		return role != null ? role.name() : ROLE_ANONYMOUS.name();
	}

	@Override
	public Role convertToEntityAttribute(String role) {
		Role r = ROLE_ANONYMOUS;

		if (role != null) {
			try {
				r = Role.valueOf(role);
			} catch (IllegalArgumentException e) {
				LOGGER.error("Unvalid role: {} retrieved from database, defaulting to ROLE_{}.", role, r);
			}
		}

		return r;
	}
}
