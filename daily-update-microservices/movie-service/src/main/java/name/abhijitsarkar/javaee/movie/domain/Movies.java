package name.abhijitsarkar.javaee.movie.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import name.abhijitsarkar.javaee.common.domain.Movie;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movies implements Serializable {
    private static final long serialVersionUID = 5890868013684382576L;

    @JsonProperty("results")
    private Collection<Movie> movies;
}
