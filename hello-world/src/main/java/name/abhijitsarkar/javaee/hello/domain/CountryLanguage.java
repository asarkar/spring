package name.abhijitsarkar.javaee.hello.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Abhijit Sarkar
 */
@Entity
@Table(name = "countrylanguage")
public class CountryLanguage {
    @EmbeddedId
    private CountryLanguageId id;

    @MapsId("countryCode")
    @OneToOne
    @JoinColumn(name = "countrycode", referencedColumnName = "code")
    private Country country;

    public String getLanguage() {
        return id.getLanguage();
    }

    public String getCountry() {
        return country.getName();
    }
}
