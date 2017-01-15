package name.abhijitsarkar.javaee.userpref.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    private String username;
    @Column(name = "PARTNER_ID")
    private String partnerId;
}
