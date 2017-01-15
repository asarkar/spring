package name.abhijitsarkar.javaee.hello.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author Abhijit Sarkar
 */
@Data
@Entity
public class City {
    @Id
    @Column
    private long id;

    @Column
    private String name;

    @OneToOne
    @JoinColumn(name = "countrycode", referencedColumnName = "code")
    private Country country;

    @Column
    private long population;

    public String getCountry() {
        return country.getName();
    }
}
