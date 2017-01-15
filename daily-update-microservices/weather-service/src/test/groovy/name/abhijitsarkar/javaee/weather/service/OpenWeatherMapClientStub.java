package name.abhijitsarkar.javaee.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import name.abhijitsarkar.javaee.weather.domain.OpenWeatherMapWeather;
import name.abhijitsarkar.javaee.weather.repository.OpenWeatherMapClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * @author Abhijit Sarkar
 */
public class OpenWeatherMapClientStub implements OpenWeatherMapClient {
    private ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

    @Override
    public OpenWeatherMapWeather getWeatherByZipCodeAndCountry(int zipCode, String units) {
        try (InputStream is = getClass().getResourceAsStream("/weather-zip.json")) {
            ObjectReader reader = objectMapper.reader();

            return reader.forType(OpenWeatherMapWeather.class).<OpenWeatherMapWeather>readValue(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
