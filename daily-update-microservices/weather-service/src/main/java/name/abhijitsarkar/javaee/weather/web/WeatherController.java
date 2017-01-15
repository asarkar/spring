package name.abhijitsarkar.javaee.weather.web;

import name.abhijitsarkar.javaee.common.domain.Weather;
import name.abhijitsarkar.javaee.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(method = GET, produces = "application/json")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @RequestMapping(value = "weather")
    public Weather getWeatherByZipCodeAndCountry(
            @RequestParam("zipCode") int zipCode,
            @RequestParam(value = "countryCode", required = false, defaultValue = "us") String countryCode,
            @RequestParam(value = "units", required = false, defaultValue = "imperial") String units) {
        return weatherService.getWeatherByZipCodeAndCountry(zipCode, countryCode, units);
    }
}
