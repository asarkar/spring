package name.abhijitsarkar.javaee.movie.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genre implements Serializable {
    private static final long serialVersionUID = 6011832025555733658L;

    private int id;
    private String name;
}
