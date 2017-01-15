package name.abhijitsarkar.javaee.weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.abhijitsarkar.javaee.common.domain.Location;
import name.abhijitsarkar.javaee.common.domain.Wind;

import java.io.Serializable;

/**
 * @author Abhijit Sarkar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = OpenWeatherMapWeatherDeserializer.class)
public class OpenWeatherMapWeather implements Serializable {
    private static final long serialVersionUID = -4115235436210959381L;

    private Location location;
    private long dateTime;
    private String summary;
    private String description;
    private double temperature;
    private double pressure;
    private double humidity;
    private Wind wind;
    private double cloudiness;
    private double rainVolInLast3Hr;
    private double snowVolInLast3Hr;
}
