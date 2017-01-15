package name.abhijitsarkar.javaee.salon.appointment.service;

import java.time.OffsetDateTime;

import org.springframework.core.convert.converter.Converter;

public class OffsetDateTimeConverter implements Converter<String, OffsetDateTime> {
	private final name.abhijitsarkar.javaee.salon.domain.OffsetDateTimeConverter converter = new name.abhijitsarkar.javaee.salon.domain.OffsetDateTimeConverter();

	@Override
	public OffsetDateTime convert(String dateTime) {
		return converter.parse(dateTime);
	}
}
