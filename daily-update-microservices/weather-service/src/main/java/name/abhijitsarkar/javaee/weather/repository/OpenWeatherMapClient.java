package name.abhijitsarkar.javaee.weather.repository;

import name.abhijitsarkar.javaee.weather.domain.OpenWeatherMapWeather;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "open-weather-map",
        url = "${open-weather-map.base-url}?APPID=${open-weather-map.api-key}")
@CacheConfig(cacheResolver = "cacheResolver")
public interface OpenWeatherMapClient {
    @RequestMapping(value = "weather",
            method = GET, produces = "application/json")
    @Cacheable(keyGenerator = "cacheKeyGenerator")
    public OpenWeatherMapWeather getWeatherByZipCodeAndCountry(
            @RequestParam("zip") int zipCode,
            @RequestParam("units") String units);
}
