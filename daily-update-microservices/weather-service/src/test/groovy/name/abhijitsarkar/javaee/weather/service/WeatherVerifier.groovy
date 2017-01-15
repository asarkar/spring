package name.abhijitsarkar.javaee.weather.service

import static spock.util.matcher.HamcrestMatchers.closeTo

/**
 * @author Abhijit Sarkar
 */
class WeatherVerifier {
    /* Doesn't care what the type of weather object is, as long as it has the methods invoked. */

    static void verifyWeather(weather) {
        assert weather

        assert weather.location
        assert weather.location.name == 'White Center'
        assert weather.location.countryCode == 'US'
        assert weather.location.latitude, closeTo(47.52d, 0.1d)
        assert weather.location.longitude, closeTo(-122.35d, 0.1d)

        assert weather.dateTime == 1451875616
        assert weather.summary == 'Snow'
        assert weather.description == 'light snow'
        assert weather.temperature, closeTo(-34.34d, 0.1d)
        assert weather.pressure, closeTo(1013d, 0.1d)
        assert weather.humidity, closeTo(100d, 0.1d)

        assert weather.wind
        assert weather.wind.speed, closeTo(8.86d, 0.1d)
        assert weather.wind.degree, closeTo(330d, 0.1d)

        assert weather.cloudiness, closeTo(90d, 0.1d)
        assert weather.rainVolInLast3Hr == 0
        assert weather.snowVolInLast3Hr == 0
    }
}
