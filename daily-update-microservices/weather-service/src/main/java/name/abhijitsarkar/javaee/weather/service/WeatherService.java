package name.abhijitsarkar.javaee.weather.service;

import name.abhijitsarkar.javaee.common.domain.Weather;
import name.abhijitsarkar.javaee.weather.domain.OpenWeatherMapWeather;
import name.abhijitsarkar.javaee.weather.repository.OpenWeatherMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author Abhijit Sarkar
 */
@Service
public class WeatherService {
    @Autowired
    OpenWeatherMapClient openWeatherMapClient;

    public Weather getWeatherByZipCodeAndCountry(int zipCode, String countryCode, String units) {
        /* Country code is ignored because OpenWeatherMap expects it with the zip separated by comma.
         * Like 98106,us. Acc. to RFC 3986, comma is a reserved character in the URL and must be encoded.
         * However, when encoded, OpenWeatherMap chokes.
         */
        OpenWeatherMapWeather openWeatherMapWeather =
                openWeatherMapClient.getWeatherByZipCodeAndCountry(zipCode, units.toLowerCase());

        Weather weather = new Weather();

        copyProperties(openWeatherMapWeather, weather);

        return weather;
    }
}
