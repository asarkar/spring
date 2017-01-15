package name.abhijitsarkar.javaee.weather.service

import name.abhijitsarkar.javaee.common.domain.Weather
import name.abhijitsarkar.javaee.weather.repository.OpenWeatherMapClient
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Abhijit Sarkar
 */
class WeatherServiceSpec extends Specification {
    @Shared
    WeatherService weatherService = new WeatherService()

    def setupSpec() {
        OpenWeatherMapClient openWeatherMapClient = Mock()
        weatherService.openWeatherMapClient = openWeatherMapClient

        openWeatherMapClient.getWeatherByZipCodeAndCountry(98106, "imperial") >>
                new OpenWeatherMapClientStub().getWeatherByZipCodeAndCountry(98106, "imperial")
    }

    def "gets weather by zip code and country"() {
        when:
        Weather weather = weatherService.getWeatherByZipCodeAndCountry(zip, country, units)

        then:
        WeatherVerifier.verifyWeather(weather)

        where:
        zip   | country | units
        98106 | 'us'    | 'imperial'
        98106 | 'US'    | 'IMPERIAL'
    }
}
