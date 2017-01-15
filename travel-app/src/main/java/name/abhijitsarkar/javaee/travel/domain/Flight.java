package name.abhijitsarkar.javaee.travel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * @author Abhijit Sarkar
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    private String srcAirportName;
    private String srcCity;
    private String srcCountry;
    private String srcFaaCode;
    private ZoneOffset srcTimeZoneOffset;

    private String destAirportName;
    private String destCity;
    private String destCountry;
    private String destFaaCode;
    private ZoneOffset destTimeZoneOffset;

    private int stops;
    private String aircraft;
    private String airline;
    private String flightNum;
    private LocalTime departureTime;
    private LocalDate departureDate;
}
