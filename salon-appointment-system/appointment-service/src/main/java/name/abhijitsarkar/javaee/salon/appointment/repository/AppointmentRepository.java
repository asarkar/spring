package name.abhijitsarkar.javaee.salon.appointment.repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	// @Param is not necessary if code is compiled with -parameters
	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByUserIdIn(@Param("userIds") Collection<Long> userIds, Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByStartDateTimeGreaterThanEqual(@Param("startDateTime") OffsetDateTime startDateTime,
			Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByStartDateTimeLessThanEqual(@Param("startDateTime") OffsetDateTime startDateTime,
			Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByStartDateTimeBetween(@Param("begin") OffsetDateTime begin, @Param("end") OffsetDateTime end,
			Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByUserIdInAndStartDateTimeBetween(@Param("userIds") Collection<Long> userIds,
			@Param("begin") OffsetDateTime begin, @Param("end") OffsetDateTime end, Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThan(
			@Param("startDateTime") OffsetDateTime startDateTime, @Param("endDateTime") OffsetDateTime endDateTime,
			Pageable p);

	@PostFilter("hasPermission(filterObject, 'read')")
	Page<Appointment> findAll(Pageable pageable);

	@PostFilter("hasPermission(filterObject, 'read')")
	List<Appointment> findAll();

	@PostFilter("hasPermission(filterObject, 'read')")
	List<Appointment> findAll(Sort sort);

	@PostFilter("hasPermission(filterObject, 'read')")
	List<Appointment> findAll(Iterable<Long> ids);

	@PostFilter("hasPermission(filterObject, 'read')")
	<S extends Appointment> List<S> save(Iterable<S> entities);

	@PostAuthorize("hasPermission(returnObject, 'read')")
	Appointment findOne(Long id);
}
