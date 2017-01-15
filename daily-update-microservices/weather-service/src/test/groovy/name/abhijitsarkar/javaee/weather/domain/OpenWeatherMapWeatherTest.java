package name.abhijitsarkar.javaee.weather.domain;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Abhijit Sarkar
 */
public class OpenWeatherMapWeatherTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

    @Test
    public void testDeserializeWeatherByZipCode() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/weather-zip.json")) {
            ObjectReader reader = objectMapper.reader();

            OpenWeatherMapWeather weather = reader.forType(OpenWeatherMapWeather.class).<OpenWeatherMapWeather>readValue(is);

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

    @Test(expected = IOException.class)
    public void testDeserializeException() throws IOException {
        JsonFactory factory = new JsonFactory();
        String carJson =
                "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
        JsonParser parser = factory.createParser(carJson);
        parser.setCodec(ObjectMapperFactory.newInstance());

        new OpenWeatherMapWeatherDeserializer().deserialize(parser, null);
    }
}
