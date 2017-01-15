package name.abhijitsarkar.javaee.weather.service;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import name.abhijitsarkar.javaee.common.domain.Weather;
import name.abhijitsarkar.javaee.weather.repository.OpenWeatherMapClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Abhijit Sarkar
 */
@RunWith(JMockit.class)
public class WeatherServiceTest {
    @Mocked
    private OpenWeatherMapClient openWeatherMapClient;

    private OpenWeatherMapClientStub stub = new OpenWeatherMapClientStub();

    private WeatherService weatherService = new WeatherService();

    @Before
    public void before() {
        weatherService.openWeatherMapClient = openWeatherMapClient;

        new Expectations() {{
            openWeatherMapClient.getWeatherByZipCodeAndCountry(98106, "imperial");
            result = stub.getWeatherByZipCodeAndCountry(98106, "imperial");
        }};
    }

    @Test
    public void testGetWeatherByZipCodeAndCountry() {
        Weather weather = weatherService.getWeatherByZipCodeAndCountry(98106, "us", "imperial");

        verifyWeather(weather);
    }

    @Test
    public void testGetWeatherByZipCodeAndCountryCaseInsensitive() {
        Weather weather = weatherService.getWeatherByZipCodeAndCountry(98106, "US", "IMPERIAL");

        verifyWeather(weather);
    }

    private void verifyWeather(Weather weather) {
        assertNotNull(weather);

        assertEquals("White Center", weather.getLocation().getName());
        assertEquals("US", weather.getLocation().getCountryCode());
        assertEquals(47.52, weather.getLocation().getLatitude(), 0.01d);
        assertEquals(-122.35, weather.getLocation().getLongitude(), 0.01d);

        assertEquals(1451875616, weather.getDateTime());
        assertEquals("Snow", weather.getSummary());
        assertEquals("light snow", weather.getDescription());
        assertEquals(34.34, weather.getTemperature(), 0.01d);
        assertEquals(1013d, weather.getPressure(), 0.01d);
        assertEquals(100d, weather.getHumidity(), 0.01d);

        assertEquals(8.86d, weather.getWind().getSpeed(), 0.01d);
        assertEquals(330d, weather.getWind().getDegree(), 0.01d);

        assertEquals(90d, weather.getCloudiness(), 0.01d);
        assertEquals(0.0d, weather.getRainVolInLast3Hr(), 0.01d);
        assertEquals(0.0d, weather.getSnowVolInLast3Hr(), 0.01d);
    }
}
