package name.abhijitsarkar.javaee.salon.appointment.repository;

import static name.abhijitsarkar.javaee.salon.appointment.AppointmentTestHelper.createNewAppointment;
import static name.abhijitsarkar.javaee.salon.appointment.AppointmentTestHelper.getAppointmentAsJson;
import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.OffsetDateTime;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import name.abhijitsarkar.javaee.salon.appointment.TestAppointmentApp;
import name.abhijitsarkar.javaee.salon.appointment.VerifyFindResult;
import name.abhijitsarkar.javaee.salon.appointment.service.OffsetDateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.appointment.domain.BeginningOfDayAdjuster;
import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;
import name.abhijitsarkar.javaee.salon.test.IdExtractor;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestAppointmentApp.class)
@WebAppConfiguration
@ActiveProfiles({ "NoReg", "NoAuth" })
public class AppointmentRepositoryFindTest {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	private String jsonAppt;

	private MockMvc mockMvc;

	// GOTCHA ALERT: There's also a mvcConversionService; tests DO NOT use that
	@Resource(name = "defaultConversionService")
	private FormatterRegistry formatterRegistry;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AppointmentRepository appointmentRepository;

	private final OffsetDateTimeFormatter formatter = new OffsetDateTimeFormatter();

	@PostConstruct
	void init() {
		formatterRegistry.removeConvertible(String.class, OffsetDateTime.class);

		formatterRegistry.addFormatter(formatter);

		OffsetDateTime startDateTime = OffsetDateTime.now().plusHours(1);
		OffsetDateTime endDateTime = startDateTime.plusHours(1);

		jsonAppt = getAppointmentAsJson(startDateTime, endDateTime);

		mockMvc = webAppContextSetup(webApplicationContext).build();
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
	public void testFindByUserIdIn() throws Exception {
		createNewAppointment(mockMvc, jsonAppt).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult createResult) throws Exception {
				String createBody = createResult.getResponse().getContentAsString();

				String userId = OBJECT_MAPPER.readTree(createBody).path("userId").asText();

				String id = new IdExtractor().apply(createResult);

				MockHttpServletRequestBuilder findRequest = get(String.format("/appointments/search/findByUserIdIn"))
						.param("userIds", userId).accept(HAL_JSON);

				mockMvc.perform(findRequest).andDo(new VerifyFindResult(id));

			}
		});
	}

	@Test
	public void testfindByStartDateTimeGreaterThanEqual() throws Exception {
		createNewAppointment(mockMvc, jsonAppt).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult createResult) throws Exception {
				String createBody = createResult.getResponse().getContentAsString();

				String startDateTime = OBJECT_MAPPER.readTree(createBody).path("startDateTime").asText();

				String id = new IdExtractor().apply(createResult);

				MockHttpServletRequestBuilder findRequest = get(
						String.format("/appointments/search/findByStartDateTimeGreaterThanEqual"))
								.param("startDateTime", startDateTime).accept(HAL_JSON);

				mockMvc.perform(findRequest).andDo(new VerifyFindResult(id));
			}
		});
	}

	@Test
	public void testfindByStartDateTimeLessThanEqual() throws Exception {
		createNewAppointment(mockMvc, jsonAppt).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult createResult) throws Exception {
				String createBody = createResult.getResponse().getContentAsString();

				String startDateTime = OBJECT_MAPPER.readTree(createBody).path("startDateTime").asText();

				String id = new IdExtractor().apply(createResult);

				MockHttpServletRequestBuilder findRequest = get(
						String.format("/appointments/search/findByStartDateTimeLessThanEqual"))
								.param("startDateTime", startDateTime).accept(HAL_JSON);

				mockMvc.perform(findRequest).andDo(new VerifyFindResult(id));
			}
		});
	}

	@Test
	public void testfindByStartDateTimeBetween() throws Exception {
		createNewAppointment(mockMvc, jsonAppt).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult createResult) throws Exception {
				String createBody = createResult.getResponse().getContentAsString();

				String startDateTimeText = OBJECT_MAPPER.readTree(createBody).path("startDateTime").asText();

				Locale defaultLocale = Locale.getDefault();

				OffsetDateTime startDateTime = formatter.parse(startDateTimeText, defaultLocale);

				String begin = formatter.print(startDateTime.minusMinutes(30), defaultLocale);
				String end = formatter.print(startDateTime.plusMinutes(30), defaultLocale);

				String id = new IdExtractor().apply(createResult);

				MockHttpServletRequestBuilder findRequest = get(
						String.format("/appointments/search/findByStartDateTimeBetween")).param("begin", begin)
								.param("end", end).accept(HAL_JSON);

				mockMvc.perform(findRequest).andDo(new VerifyFindResult(id));
			}
		});
	}

	@Test
	public void testFindTodaysSchedule() throws Exception {
		createNewAppointment(mockMvc, jsonAppt).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult createResult) throws Exception {
				String createBody = createResult.getResponse().getContentAsString();

				String startDateTimeText = OBJECT_MAPPER.readTree(createBody).path("startDateTime").asText();

				Locale defaultLocale = Locale.getDefault();

				OffsetDateTime startDateTime = formatter.parse(startDateTimeText, defaultLocale);

				String begin = formatter.print(startDateTime.with(new BeginningOfDayAdjuster()), defaultLocale);
				String end = formatter.print(startDateTime.plusDays(1), defaultLocale);

				String id = new IdExtractor().apply(createResult);

				MockHttpServletRequestBuilder findRequest = get(
						String.format("/appointments/search/findByStartDateTimeBetween")).param("begin", begin)
								.param("end", end).accept(HAL_JSON);

				mockMvc.perform(findRequest).andDo(new VerifyFindResult(id));
			}
		});
	}
}
