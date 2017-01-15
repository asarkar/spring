package name.abhijitsarkar.javaee.salon.test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;

public class IdExtractor implements Function<MvcResult, String> {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	@Override
	public String apply(MvcResult result) {
		try {
			String body = result.getResponse().getContentAsString();

			String uri = OBJECT_MAPPER.readTree(body).path("_links").path("self").path("href").asText();

			return uri.substring(uri.lastIndexOf('/') + 1);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
