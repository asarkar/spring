package name.abhijitsarkar.javaee.weather.domain

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import name.abhijitsarkar.javaee.common.ObjectMapperFactory
import name.abhijitsarkar.javaee.weather.service.WeatherVerifier
import spock.lang.Specification


/**
 * @author Abhijit Sarkar
 */
class OpenWeatherMapWeatherSpec extends Specification {
    ObjectMapper objectMapper = ObjectMapperFactory.newInstance()

    def "deserializes weather"() {
        setup:
        InputStream is = getClass().getResourceAsStream("/weather-zip.json")
        ObjectReader reader = objectMapper.reader()
        OpenWeatherMapWeather weather = null

        when:
        is.withCloseable {
            weather = reader.forType(OpenWeatherMapWeather.class).<OpenWeatherMapWeather> readValue(is);
        }

        then:
        WeatherVerifier.verifyWeather(weather)
    }

    def "throws exception when unable to deserialize weather"() {
        setup:
        JsonFactory factory = new JsonFactory()
        String carJson = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }"
        JsonParser parser = factory.createParser(carJson)
        parser.setCodec(ObjectMapperFactory.newInstance())

        when:
        new OpenWeatherMapWeatherDeserializer().deserialize(parser, null)

        then:
        thrown(IOException)
    }
}