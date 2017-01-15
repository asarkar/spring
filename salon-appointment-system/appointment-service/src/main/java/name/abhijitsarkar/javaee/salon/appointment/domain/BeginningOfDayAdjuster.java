package name.abhijitsarkar.javaee.salon.appointment.domain;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

public class BeginningOfDayAdjuster implements TemporalAdjuster {
	@Override
	public Temporal adjustInto(Temporal input) {
		if (!(input instanceof OffsetDateTime)) {
			throw new DateTimeException(String.format("Cannot adjust: %s.", input.getClass().getName()));
		}

		OffsetDateTime dateTime = (OffsetDateTime) input;

		return dateTime.minusHours(dateTime.getHour()).truncatedTo(ChronoUnit.HOURS);
	}
}
