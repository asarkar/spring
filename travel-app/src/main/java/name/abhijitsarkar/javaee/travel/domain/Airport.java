package name.abhijitsarkar.javaee.travel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.ZoneOffset;

/**
 * @author Abhijit Sarkar
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    private String name;
    private String city;
    private String country;

    /* The Federal Aviation Administration identifier is a three- or four-letter alphanumeric code
     * identifying United States airports.
     */
    private String faaCode;
    /*
    * The ICAO (/ˌaɪˌkeɪˈoʊ/, "I-K-O") airport code or location indicator is a four-character alphanumeric code
    * designating aerodromes around the world. These codes are defined by the International Civil Aviation Organization.
    */
    private String icaoCode;
    private ZoneOffset timeZoneOffset;

    public void updateFrom(Airport from) {
        name = from.name;
        city = from.city;
        country = from.country;
        faaCode = from.faaCode;
        icaoCode = from.icaoCode;
        timeZoneOffset = from.timeZoneOffset;
    }
}
