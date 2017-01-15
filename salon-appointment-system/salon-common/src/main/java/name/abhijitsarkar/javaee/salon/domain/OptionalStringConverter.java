package name.abhijitsarkar.javaee.salon.domain;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OptionalStringConverter implements AttributeConverter<Optional<String>, String> {

	@Override
	public String convertToDatabaseColumn(Optional<String> opt) {
		if (opt == null || !opt.isPresent() || opt.get() == null) {
			return null;
		}

		return opt.get().toLowerCase();
	}

	@Override
	public Optional<String> convertToEntityAttribute(String opt) {
		return Optional.ofNullable(opt).map(String::toLowerCase);
	}
}
