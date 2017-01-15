package name.abhijitsarkar.javaee.hello.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Abhijit Sarkar
 */
@Data
@Entity
public class Country {
    @Id
    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String continent;

    @Column
    private String region;

    @Column
    private long population;
}
