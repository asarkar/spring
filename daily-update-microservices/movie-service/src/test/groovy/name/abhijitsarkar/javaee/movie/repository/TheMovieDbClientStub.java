package name.abhijitsarkar.javaee.movie.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import name.abhijitsarkar.javaee.movie.domain.Genres;
import name.abhijitsarkar.javaee.movie.domain.Movies;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Abhijit Sarkar
 */
public class TheMovieDbClientStub implements TheMovieDbClient {
    private Movies result;
    private Genres genres;

    public TheMovieDbClientStub() throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

        String cachedPopularMoviesUri = "/popular-movies.json";
        String cachedGenresUri = "/genres.json";

        try (InputStream is1 = getClass().getResourceAsStream(cachedPopularMoviesUri);
             InputStream is2 = getClass().getResourceAsStream(cachedGenresUri)) {
            result = objectMapper.readValue(is1, Movies.class);
            genres = objectMapper.readValue(is2, Genres.class);
        }
    }

    @Override
    public Movies findPopularMovies() {
        return result;
    }

    @Override
    public Genres getAllGenres() {
        return genres;
    }
}
