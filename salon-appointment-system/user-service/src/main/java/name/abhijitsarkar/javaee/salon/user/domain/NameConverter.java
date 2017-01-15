package name.abhijitsarkar.javaee.salon.user.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class NameConverter implements AttributeConverter<String, String> {
	@Override
	public String convertToDatabaseColumn(String name) {
		return name.toLowerCase();
	}

	@Override
	public String convertToEntityAttribute(String name) {
		return name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
	}
}
