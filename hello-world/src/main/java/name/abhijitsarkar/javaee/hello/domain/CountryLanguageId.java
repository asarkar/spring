package name.abhijitsarkar.javaee.hello.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Abhijit Sarkar
 */
@Data
@Embeddable
public class CountryLanguageId implements Serializable {
    private String countryCode;

    private String language;
}
