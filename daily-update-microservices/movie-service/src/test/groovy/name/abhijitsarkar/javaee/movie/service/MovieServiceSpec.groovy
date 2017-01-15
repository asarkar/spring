package name.abhijitsarkar.javaee.movie.service

import name.abhijitsarkar.javaee.common.domain.Movie
import name.abhijitsarkar.javaee.movie.repository.TheMovieDbClientStub
import spock.lang.Shared
import spock.lang.Specification


/**
 * @author Abhijit Sarkar
 */
class MovieServiceSpec extends Specification {
    @Shared
    MovieService movieService = new MovieService()

    def setupSpec() {
        movieService.movieDbClient = new TheMovieDbClientStub()
    }

    def "correctly maps genre id to name"() {
        setup:
        Map<Integer, String> expectedGenreIdToNameMap = [28: 'Action', 80: 'Crime', 53: 'Thriller']

        when:
        Map<Integer, String> actualGenreIdToNameMap = movieService.genreIdToNameMap

        then:
        actualGenreIdToNameMap.findAll { expectedGenreIdToNameMap.containsKey(it.key) } == expectedGenreIdToNameMap
    }

    def "finds popular movies"() {
        when:
        Collection<Movie> popularMovies = movieService.findPopularMovies()

        then:
        popularMovies.any { it.title.toLowerCase().contains('star wars') }
    }
}