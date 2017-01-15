package name.abhijitsarkar.javaee.salon.appointment.web;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import name.abhijitsarkar.javaee.salon.appointment.domain.BeginningOfDayAdjuster;
import name.abhijitsarkar.javaee.salon.appointment.repository.AppointmentRepository;
import name.abhijitsarkar.javaee.salon.appointment.service.UserService;

@RepositoryRestController
@RequestMapping(value = "/appointments/search", method = GET)
public class AppointmentController {
	private static final TemporalAdjuster BEGINNING_OF_DAY_ADJUSTER = new BeginningOfDayAdjuster();

	@Autowired
	private AppointmentRepository apptRepo;

	@Autowired
	private PagedResourcesAssembler<Appointment> pagedResourcesAssembler;

	@Autowired
	private UserService userService;

	@RequestMapping("/findAllToday")
	public HttpEntity<PagedResources<Resource<Appointment>>> findAllToday(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable) {
		OffsetDateTime beginningOfDay = OffsetDateTime.now().with(BEGINNING_OF_DAY_ADJUSTER);
		OffsetDateTime endOfDay = OffsetDateTime.from(beginningOfDay).plusDays(1);

		Page<Appointment> todaysSchedule = apptRepo.findByStartDateTimeBetween(beginningOfDay, endOfDay, pageable);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		PagedResources<Resource<Appointment>> resource = pagedResourcesAssembler.toResource(todaysSchedule,
				(ResourceAssembler) entityAssembler);

		return new ResponseEntity<>(resource, OK);
	}

	@RequestMapping("/findByFirstName")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstName(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName) {
		Collection<Long> userIds = userService.getUserIdsByFirstName(firstName);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByLastName")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByLastName(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("lastName") String lastName) {
		Collection<Long> userIds = userService.getUserIdsByLastName(lastName);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByFirstAndLastNames")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstAndLastNames(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
		Collection<Long> userIds = userService.getUserIdsByFirstAndLastNames(firstName, lastName);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByFirstNameAndStartsToday")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstNameAndStartsToday(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName) {
		OffsetDateTime beginningOfDay = OffsetDateTime.now().with(BEGINNING_OF_DAY_ADJUSTER);

		return findByFirstNameAndStartsOnOrAfterDateTime(entityAssembler, pageable, firstName, beginningOfDay);
	}

	@RequestMapping("/findByLastNameAndStartsToday")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByLastNameAndStartsToday(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("lastName") String lastName) {
		OffsetDateTime beginningOfDay = OffsetDateTime.now().with(BEGINNING_OF_DAY_ADJUSTER);

		return findByLastNameAndStartsOnOrAfterDateTime(entityAssembler, pageable, lastName, beginningOfDay);
	}

	@RequestMapping("/findByFirstAndLastNamesAndStartsToday")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstAndLastNamesAndStartsToday(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
		OffsetDateTime beginningOfDay = OffsetDateTime.now().with(BEGINNING_OF_DAY_ADJUSTER);

		return findByFirstAndLastNamesAndStartsOnOrAfterDateTime(entityAssembler, pageable, firstName, lastName, beginningOfDay);
	}

	@RequestMapping("/findByFirstNameAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstNameAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName, @RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByFirstName(firstName);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	@RequestMapping("/findByLastNameAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByLastNameAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("lastName") String lastName, @RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByLastName(lastName);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	@RequestMapping("/findByFirstAndLastNamesAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByFirstAndLastNamesAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByFirstAndLastNames(firstName, lastName);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	@RequestMapping("/findByEmail")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByEmail(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable, @RequestParam("email") String email) {
		Collection<Long> userIds = userService.getUserIdsByEmail(email);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByEmailAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByEmailAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable, @RequestParam("email") String email,
			@RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByEmail(email);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	@RequestMapping("/findByPhoneNum")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByPhoneNum(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("phoneNum") String phoneNum) {
		Collection<Long> userIds = userService.getUserIdsByPhoneNum(phoneNum);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByPhoneNumAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByPhoneNumAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("phoneNum") String phoneNum, @RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByPhoneNum(phoneNum);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	@RequestMapping("/findByPhoneNumEndingWith")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByPhoneNumEndingWith(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("phoneNum") String phoneNum) {
		Collection<Long> userIds = userService.getUserIdsByPhoneNumEndingWith(phoneNum);

		return findByUserIdIn(entityAssembler, pageable, userIds);
	}

	@RequestMapping("/findByPhoneNumEndingWithAndStartsOnOrAfterDateTime")
	public HttpEntity<PagedResources<Resource<Appointment>>> findByPhoneNumEndingWithAndStartsOnOrAfterDateTime(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable,
			@RequestParam("phoneNum") String phoneNum, @RequestParam("startDateTime") OffsetDateTime startDateTime) {
		OffsetDateTime endOfDay = OffsetDateTime.from(startDateTime).plusDays(1).with(BEGINNING_OF_DAY_ADJUSTER);

		Collection<Long> userIds = userService.getUserIdsByPhoneNumEndingWith(phoneNum);

		return findByUserIdInAndStartDateTimeBetween(entityAssembler, pageable, userIds, startDateTime, endOfDay);
	}

	private HttpEntity<PagedResources<Resource<Appointment>>> findByUserIdIn(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable, Collection<Long> userIds) {
		Page<Appointment> todaysSchedule = apptRepo.findByUserIdIn(userIds, pageable);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		PagedResources<Resource<Appointment>> resource = pagedResourcesAssembler.toResource(todaysSchedule,
				(ResourceAssembler) entityAssembler);

		return new ResponseEntity<>(resource, OK);
	}

	private HttpEntity<PagedResources<Resource<Appointment>>> findByUserIdInAndStartDateTimeBetween(
			PersistentEntityResourceAssembler entityAssembler, Pageable pageable, Collection<Long> userIds,
			OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
		Page<Appointment> todaysSchedule = apptRepo.findByUserIdInAndStartDateTimeBetween(userIds, startDateTime,
				endDateTime, pageable);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		PagedResources<Resource<Appointment>> resource = pagedResourcesAssembler.toResource(todaysSchedule,
				(ResourceAssembler) entityAssembler);

		return new ResponseEntity<>(resource, OK);
	}
}
