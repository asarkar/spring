package name.abhijitsarkar.javaee.salon.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OffsetDateTimeConverter {
	private static final TypeReference<Map<OffsetDateTime, String>> TYPE_REF = new TypeReference<Map<OffsetDateTime, String>>() {
	};

	/**
	 * Instead of using DateTimeFormatter, use Jackson for consistency because
	 * that's what Spring uses for HTTP message conversion.
	 */
	private final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();

	public String format(OffsetDateTime dateTime) {
		checkArgument(dateTime != null, "Datetime must not be null.");
		
		try {
			return mapper.writeValueAsString(dateTime).replaceAll("\"", "");
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	public OffsetDateTime parse(String dateTime) {
		checkArgument(dateTime != null, "Datetime must not be null.");

		try {
			Map<OffsetDateTime, String> value = mapper.readValue(map(dateTime, "dummy"), TYPE_REF);

			return value.keySet().iterator().next();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String map(String key, String value) {
		return String.format("{\"%s\":\"%s\"}", key, value);
	}
}
