package name.abhijitsarkar.javaee.travel.web;

import name.abhijitsarkar.javaee.travel.domain.Flight;
import name.abhijitsarkar.javaee.travel.domain.Page;
import name.abhijitsarkar.javaee.travel.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(method = GET, path = "/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @RequestMapping
    Page<Flight> findFlights(@RequestParam("src") String srcAirportFaa,
                             @RequestParam("dest") String destAirportFaa,
                             @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate,
                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                             @RequestParam(name = "pageNum", defaultValue = "1") int pageNum) {
        return flightService.findFlights(srcAirportFaa, destAirportFaa, departureDate, pageSize, pageNum);
    }
}
