package name.abhijitsarkar.javaee.movie.web;

import name.abhijitsarkar.javaee.common.domain.Movie;
import name.abhijitsarkar.javaee.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(value = "movies", method = GET, produces = APPLICATION_JSON_VALUE)
public class MovieController {
    @Autowired
    private MovieService movieService;

    @RequestMapping("popular")
    public Collection<Movie> findPopularMovies() {
        return movieService.findPopularMovies();
    }
}
