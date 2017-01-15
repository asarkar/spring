package name.abhijitsarkar.javaee.salon.appointment.service;

import static java.util.stream.Collectors.toList;
import static name.abhijitsarkar.javaee.salon.domain.Role.ROLE_ADMIN;

import java.io.Serializable;
import java.util.List;

import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AppointmentPermissionEvaluator implements PermissionEvaluator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentPermissionEvaluator.class);

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		boolean hasPermission = false;

		if (targetDomainObject instanceof Appointment) {
			Appointment appt = (Appointment) targetDomainObject;

			LOGGER.debug("Verifying permission on appointment with id: {} and userId: {}.", appt.getId(),
					appt.getUserId());

			hasPermission = isAdmin(authentication) || isSelfAppointment(authentication, appt);
		}
		return hasPermission;
	}

	private boolean isAdmin(Authentication authentication) {
		List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(toList());

		LOGGER.info("Principal: {}, authorities: {}.", authentication.getPrincipal(), authorities);

		return authorities.contains(ROLE_ADMIN.name());
	}

	private boolean isSelfAppointment(Authentication authentication, Appointment appt) {
		boolean isSelfAppointment = appt.getUserId().equals(authentication.getDetails());

		LOGGER.info("Appointment id: {}, user id: {}, authentication details: {}.", appt.getId(), appt.getUserId(),
				authentication.getDetails());

		return isSelfAppointment;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}
}
