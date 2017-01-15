package name.abhijitsarkar.javaee.dailyupdate.repository;

import name.abhijitsarkar.javaee.common.domain.Movie;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "movie-service")
public interface MovieServiceClient {
    @RequestMapping(value = "/movies/popular",
            method = GET, produces = APPLICATION_JSON_VALUE)
    public Collection<Movie> findPopularMovies();
}
