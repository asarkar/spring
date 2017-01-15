package name.abhijitsarkar.javaee.userpref.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SERVICES")
@Data
@NoArgsConstructor
public class ServiceEntity {
    @Id
    private int id;
    private String name;
}
