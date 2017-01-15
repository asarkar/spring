package name.abhijitsarkar.javaee.movie.service;

import name.abhijitsarkar.javaee.common.domain.Movie;
import name.abhijitsarkar.javaee.movie.repository.TheMovieDbClientStub;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Abhijit Sarkar
 */
public class MovieServiceTest {
    private MovieService movieService = new MovieService();

    public MovieServiceTest() throws IOException {
        movieService.movieDbClient = new TheMovieDbClientStub();
    }

    @Test
    public void testGetGenreIdToNameMap() {
        Map<Integer, String> genreIdToNameMap = movieService.getGenreIdToNameMap();

        assertEquals("Action", genreIdToNameMap.get(28));
        assertEquals("Crime", genreIdToNameMap.get(80));
        assertEquals("Thriller", genreIdToNameMap.get(53));
    }

    @Test
    public void testFindPopularMovies() {
        Collection<Movie> popularMovies = movieService.findPopularMovies();

        assertTrue(popularMovies.stream().anyMatch(m -> m.getTitle().toLowerCase().contains("star wars")));
    }
}
