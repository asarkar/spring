package name.abhijitsarkar.javaee.salon.appointment.repository;

import static java.util.Arrays.asList;
import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import name.abhijitsarkar.javaee.salon.appointment.TestAppointmentApp;
import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;
import name.abhijitsarkar.javaee.salon.test.ContentMatcher;
import name.abhijitsarkar.javaee.salon.test.DeleteAndCompare;
import name.abhijitsarkar.javaee.salon.test.GetAndCompare;
import name.abhijitsarkar.javaee.salon.test.IdExtractor;
import name.abhijitsarkar.javaee.salon.test.Pair;
import name.abhijitsarkar.javaee.salon.test.UpdateAndCompare;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestAppointmentApp.class)
@WebAppConfiguration
@ActiveProfiles({ "NoReg", "NoAuth" })
public class AppointmentRepositoryCrudTest {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	private String jsonAppt;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@PostConstruct
	void init() throws JsonProcessingException {
		mockMvc = webAppContextSetup(webApplicationContext).build();

		OffsetDateTime startDateTime = OffsetDateTime.now().plusHours(1);
		OffsetDateTime endDateTime = startDateTime.plusHours(1);

		Appointment testAppt = new Appointment().withUserId(1l).withStartDateTime(startDateTime)
				.withEndDateTime(endDateTime);

		jsonAppt = OBJECT_MAPPER.writeValueAsString(testAppt);
	}

	@Before
	public void setUp() {
		appointmentRepository.deleteAllInBatch();
	}

	@After
	public void tearDown() {
		appointmentRepository.deleteAllInBatch();
	}

	@Test
	public void testCreateAppointment() throws Exception {
		Pair pair = new Pair(asList("_links", "self", "href"), ".*/appointments/\\d+$");

		createNewAppointment().andExpect(content().string(new ContentMatcher(pair)));
	}

	@Test
	public void testGetAppointment() throws Exception {
		Pair pair = new Pair(asList("_links", "self", "href"), ".*/appointments/\\d+$");

		createNewAppointment().andDo(new GetAndCompare(pair, mockMvc, "/appointments/%s"));
	}

	@Test
	public void testUpdateAppointment() throws Exception {
		OffsetDateTime startDateTime = OffsetDateTime.now().plusHours(2);

		String startDateTimeText = ObjectMapperFactory.newObjectMapper().writeValueAsString(startDateTime)
				.replaceAll("\"", "");

		AppointmentExtractor AppointmentExtractor = new AppointmentExtractor(startDateTime);
		Pair pair = new Pair(asList("startDateTime"), startDateTimeText);

		createNewAppointment()
				.andDo(new UpdateAndCompare<Appointment>(pair, mockMvc, "/appointments/%s", AppointmentExtractor));
	}

	@Test
	public void testDeleteAppointment() throws Exception {
		createNewAppointment().andDo(new DeleteAndCompare(mockMvc, "/appointments/%s"));
	}

	private ResultActions createNewAppointment() throws Exception {
		return mockMvc.perform(post("/appointments").content(jsonAppt).contentType(APPLICATION_JSON).accept(HAL_JSON))
				.andExpect(status().isCreated()).andExpect(content().contentType(HAL_JSON));
	}

	private final class AppointmentExtractor implements Function<MvcResult, Appointment> {
		private final OffsetDateTime newStartTime;

		private AppointmentExtractor(OffsetDateTime newStartTime) {
			this.newStartTime = newStartTime;
		}

		@Override
		public Appointment apply(MvcResult result) {
			try {
				String appointmentId = new IdExtractor().apply(result);

				String body = result.getResponse().getContentAsString();
				JsonNode getTree = OBJECT_MAPPER.readTree(body);
				long userId = getTree.path("userId").asLong();

				return new Appointment().withId(Long.valueOf(appointmentId)).withStartDateTime(newStartTime)
						.withEndDateTime(newStartTime.plusHours(2)).withUserId(userId);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
