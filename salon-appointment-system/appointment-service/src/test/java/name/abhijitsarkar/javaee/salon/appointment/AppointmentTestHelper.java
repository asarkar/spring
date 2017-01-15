package name.abhijitsarkar.javaee.salon.appointment;

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UncheckedIOException;
import java.time.OffsetDateTime;

import name.abhijitsarkar.javaee.salon.appointment.domain.Appointment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;

public class AppointmentTestHelper {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	public static String getAppointmentAsJson(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
		Appointment testAppt = new Appointment().withUserId(1l).withStartDateTime(startDateTime).withEndDateTime(endDateTime);

		try {
			return OBJECT_MAPPER.writeValueAsString(testAppt);
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static ResultActions createNewAppointment(MockMvc mockMvc, String jsonAppt) throws Exception {
		return mockMvc.perform(post("/appointments").content(jsonAppt).contentType(APPLICATION_JSON).accept(HAL_JSON))
				.andExpect(status().isCreated()).andExpect(content().contentType(HAL_JSON));
	}
}
