package name.abhijitsarkar.javaee.salon.appointment.web;

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.OffsetDateTime;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.appointment.TestAppointmentApp;
import name.abhijitsarkar.javaee.salon.appointment.VerifyFindResult;
import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import name.abhijitsarkar.javaee.salon.appointment.repository.AppointmentRepository;
import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestAppointmentApp.class)
@WebAppConfiguration
@ActiveProfiles({ "NoReg", "NoAuth" })
public class AppointmentControllerTest {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AppointmentRepository appointmentRepository;

	private Appointment appt;

	@PostConstruct
	void init() throws JsonProcessingException {
		mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Before
	public void setUp() throws JsonProcessingException {
		appointmentRepository.deleteAllInBatch();

		createNewAppointment();
	}

	private void createNewAppointment() {
		OffsetDateTime startTime = OffsetDateTime.now().plusHours(1);
		OffsetDateTime endTime = startTime.plusHours(1);

		appt = new Appointment().withUserId(1l).withStartDateTime(startTime).withEndDateTime(endTime);

		appt = appointmentRepository.save(appt);
	}

	@After
	public void tearDown() {
		appointmentRepository.deleteAllInBatch();
	}

	@Test
	public void testFindTodaysSchedule() throws Exception {
		mockMvc.perform(get("/appointments/search/findAllToday").accept(HAL_JSON))
				.andDo(new VerifyFindResult(String.valueOf(appt.getId())));
	}

	@Test
	public void testFindByFirstNameAndStartsToday() throws Exception {
		mockMvc.perform(
				get("/appointments/search/findByFirstNameAndStartsToday").param("firstName", "john").accept(HAL_JSON))
				.andDo(new VerifyFindResult(String.valueOf(appt.getId())));
	}

	@Test
	public void testFindByLastNameAndStartsToday() throws Exception {
		mockMvc.perform(
				get("/appointments/search/findByLastNameAndStartsToday").param("lastName", "doe").accept(HAL_JSON))
				.andDo(new VerifyFindResult(String.valueOf(appt.getId())));
	}

	@Test
	public void testFindByFirstNameAndStartsOnDateTime() throws Exception {
		String startTime = OBJECT_MAPPER.writeValueAsString(appt.getStartDateTime()).replaceAll("\"", "");

		mockMvc.perform(get("/appointments/search/findByFirstNameAndStartsOnOrAfterDateTime").param("firstName", "john")
				.param("startDateTime", startTime).accept(HAL_JSON))
				.andDo(new VerifyFindResult(String.valueOf(appt.getId())));
	}

	@Test
	public void testFindByFirstAndLastNamesAndStartsOnDateTime() throws Exception {
		String startTime = OBJECT_MAPPER.writeValueAsString(appt.getStartDateTime()).replaceAll("\"", "");

		mockMvc.perform(get("/appointments/search/findByFirstAndLastNamesAndStartsOnOrAfterDateTime").param("firstName", "john")
				.param("lastName", "doe").param("startDateTime", startTime).accept(HAL_JSON))
				.andDo(MockMvcResultHandlers.print()).andDo(new VerifyFindResult(String.valueOf(appt.getId())));
	}
}
