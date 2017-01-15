package name.abhijitsarkar.javaee.dailyupdate.repository;

import name.abhijitsarkar.javaee.common.domain.Weather;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "weather-service")
public interface WeatherServiceClient {
    @RequestMapping(value = "weather", method = GET, produces = APPLICATION_JSON_VALUE)
    public Weather getWeatherByZipCodeAndCountry(
            @RequestParam("zipCode") int zipCode,
            @RequestParam(value = "countryCode", required = false) String countryCode,
            @RequestParam(value = "units", required = false) String units);
}
