package name.abhijitsarkar.javaee.movie.service;

import name.abhijitsarkar.javaee.common.domain.Movie;
import name.abhijitsarkar.javaee.movie.domain.Genre;
import name.abhijitsarkar.javaee.movie.repository.TheMovieDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Abhijit Sarkar
 */
@Service
@CacheConfig(cacheResolver = "cacheResolver")
public class MovieService {
    @Autowired
    TheMovieDbClient movieDbClient;

    public Collection<Movie> findPopularMovies() {
        Map<Integer, String> genreIdToNameMap = getGenreIdToNameMap();

        Collection<Movie> movies = movieDbClient.findPopularMovies().getMovies();

        movies.stream().forEach(m -> {
            List<String> genres = m.getGenreIds().stream().map(genreIdToNameMap::get).collect(toList());

            m.setGenres(genres);
        });

        return movies;
    }

    Map<Integer, String> getGenreIdToNameMap() {
        return movieDbClient.getAllGenres().
                getGenres().
                stream().
                collect(toMap(Genre::getId, Genre::getName));
    }
}
