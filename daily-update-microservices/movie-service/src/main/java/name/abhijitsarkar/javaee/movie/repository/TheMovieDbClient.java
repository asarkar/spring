package name.abhijitsarkar.javaee.movie.repository;

import name.abhijitsarkar.javaee.movie.domain.Genres;
import name.abhijitsarkar.javaee.movie.domain.Movies;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "the-movie-db",
        url = "${the-movie-db.base-url}?api_key=${the-movie-db.api-key}")
@CacheConfig(cacheResolver = "cacheResolver")
public interface TheMovieDbClient {
    @RequestMapping(value = "/3/movie/popular",
            method = GET, produces = APPLICATION_JSON_VALUE)
    @Cacheable(keyGenerator = "cacheKeyGenerator")
    public Movies findPopularMovies();

    @RequestMapping(value = "/3/genre/movie/list",
            method = GET, produces = APPLICATION_JSON_VALUE)
    @Cacheable(keyGenerator = "cacheKeyGenerator")
    public Genres getAllGenres();
}
