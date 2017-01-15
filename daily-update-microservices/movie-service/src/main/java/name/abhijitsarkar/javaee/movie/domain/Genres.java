package name.abhijitsarkar.javaee.movie.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Abhijit Sarkar
 */
@Data
public class Genres implements Serializable {
    private static final long serialVersionUID = -3593114681600265271L;

    Collection<Genre> genres;
}
