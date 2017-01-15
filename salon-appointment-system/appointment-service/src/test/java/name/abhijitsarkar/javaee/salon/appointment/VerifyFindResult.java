package name.abhijitsarkar.javaee.salon.appointment;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;
import name.abhijitsarkar.javaee.salon.test.ContentMatcher;
import name.abhijitsarkar.javaee.salon.test.Pair;

@RequiredArgsConstructor
public class VerifyFindResult implements ResultHandler {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();
	private final String id;

	@Override
	public void handle(MvcResult findResult) throws Exception {
		status().isOk().match(findResult);
		content().contentType(HAL_JSON).match(findResult);

		String findBody = findResult.getResponse().getContentAsString();

		JsonNode tree = OBJECT_MAPPER.readTree(findBody);
		JsonNode appointments = tree.path("_embedded").path("appointments");

		assertFalse(appointments.isMissingNode());
		assertTrue(appointments.has(0));

		String appointmentText = appointments.get(0).toString();

		Pair pair = new Pair(asList("_links", "self", "href"), String.format(".*/appointments/%s$", id));

		assertTrue(new ContentMatcher(pair).matches(appointmentText));
	}
}
