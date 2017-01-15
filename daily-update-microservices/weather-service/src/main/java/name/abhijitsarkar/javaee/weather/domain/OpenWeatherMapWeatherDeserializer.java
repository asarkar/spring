package name.abhijitsarkar.javaee.weather.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import name.abhijitsarkar.javaee.common.domain.Location;
import name.abhijitsarkar.javaee.common.domain.Wind;

import java.io.IOException;

/**
 * @author Abhijit Sarkar
 */
public class OpenWeatherMapWeatherDeserializer extends JsonDeserializer<OpenWeatherMapWeather> {
    @Override
    public OpenWeatherMapWeather deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode root = p.getCodec().readTree(p);

        JsonNode coordinates = root.path("coord");

        if (coordinates.isMissingNode()) {
            throw new IOException(String.format("Failed to deserialize from: %s", root));
        }

        double latitude = coordinates.path("lat").asDouble();
        double longitude = coordinates.path("lon").asDouble();
        String countryCode = root.path("sys").path("country").asText();
        String name = root.path("name").asText();

        Location location = Location.builder().
                latitude(latitude).
                longitude(longitude).
                countryCode(countryCode).
                name(name).
                build();

        JsonNode weather = root.path("weather");
        JsonNode main = root.path("main");

        String summary = weather.path(0).path("main").asText();
        String description = weather.path(0).path("description").asText();
        double temperature = main.path("temp").asDouble();
        double pressure = main.path("pressure").asDouble();
        double humidity = main.path("humidity").asDouble();

        JsonNode w = root.path("wind");

        double windSpeed = w.path("speed").asDouble();
        double windDegree = w.path("deg").asDouble();

        Wind wind = Wind.builder().
                speed(windSpeed).
                degree(windDegree).
                build();

        double cloudiness = root.path("clouds").path("all").asDouble();

        double rainVolInLast3Hr = root.path("rain").path("3h").asDouble();
        double snowVolInLast3Hr = root.path("snow").path("3h").asDouble();

        long dateTime = root.path("dt").asLong();

        return OpenWeatherMapWeather.builder().
                location(location).
                dateTime(dateTime).
                summary(summary).
                description(description).
                temperature(temperature).
                pressure(pressure).
                humidity(humidity).
                wind(wind).
                cloudiness(cloudiness).
                rainVolInLast3Hr(rainVolInLast3Hr).
                snowVolInLast3Hr(snowVolInLast3Hr).
                build();
    }
}
