package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import name.abhijitsarkar.javaee.travel.domain.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.function.Function;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_TIME;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_AIRCRAFT;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_AIRLINE;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_DEPARTURE_DAY;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_DEPARTURE_TIME;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_DEST_AIRPORT_FAA;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_FLIGHT_NUM;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_SRC_AIRPORT_FAA;
import static name.abhijitsarkar.javaee.travel.repository.RouteRepositoryImpl.FIELD_STOPS;

/**
 * @author Abhijit Sarkar
 */
public class N1qlQueryRowToRouteMapper implements Function<N1qlQueryRow, Route> {
    private static final Logger LOGGER = LoggerFactory.getLogger(N1qlQueryRowToRouteMapper.class);

    @Override
    public Route apply(N1qlQueryRow row) {
        JsonObject value = row.value();

        try {
            String departureTime = value.getString(FIELD_DEPARTURE_TIME);
            OffsetTime departureTimeUTC = OffsetTime.of(LocalTime.parse(departureTime, ISO_TIME), UTC);

            return Route.builder()
                    .srcAirportFaa(value.getString(FIELD_SRC_AIRPORT_FAA))
                    .destAirportFaa(value.getString(FIELD_DEST_AIRPORT_FAA))
                    .stops(value.getInt(FIELD_STOPS))
                    .aircraft(value.getString(FIELD_AIRCRAFT))
                    .airline(value.getString(FIELD_AIRLINE))
                    .flightNum(value.getString(FIELD_FLIGHT_NUM))
                    .departureTimeUTC(departureTimeUTC)
                    .departureDay(DayOfWeek.of(value.getInt(FIELD_DEPARTURE_DAY) + 1)).build();
        } catch (Exception e) {
            LOGGER.error("Failed to convert result row: {} to route object.", row, e);

            return null;
        }
    }
}
