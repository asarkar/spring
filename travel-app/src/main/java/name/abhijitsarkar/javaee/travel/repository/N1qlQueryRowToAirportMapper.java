package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import name.abhijitsarkar.javaee.travel.domain.Airport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_CITY;
import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_COUNTRY;
import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_FAA;
import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_ICAO;
import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_NAME;
import static name.abhijitsarkar.javaee.travel.repository.AirportRepositoryImpl.FIELD_TIMEZONE;

/**
 * @author Abhijit Sarkar
 */
public class N1qlQueryRowToAirportMapper implements Function<N1qlQueryRow, Airport> {
    private static final Logger LOGGER = LoggerFactory.getLogger(N1qlQueryRowToAirportMapper.class);

    @Override
    public Airport apply(N1qlQueryRow row) {
        JsonObject value = row.value();

        try {
            /* Get current time at airport timezone */
            ZonedDateTime now = Instant.now().atZone(ZoneId.of(value.getString(FIELD_TIMEZONE)));

            return Airport.builder()
                    .name(value.getString(FIELD_NAME))
                    .faaCode(value.getString(FIELD_FAA))
                    .icaoCode(value.getString(FIELD_ICAO))
                    .city(value.getString(FIELD_CITY))
                    .country(value.getString(FIELD_COUNTRY))
                    .timeZoneOffset(now.getOffset())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Failed to convert result row: {} to airport object.", row, e);

            return null;
        }
    }
}
