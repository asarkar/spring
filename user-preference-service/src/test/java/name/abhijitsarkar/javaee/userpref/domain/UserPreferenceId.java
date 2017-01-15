package name.abhijitsarkar.javaee.userpref.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceId implements Serializable {
    private static final long serialVersionUID = -267789055401234607L;

    private String name;
    @Column(name = "SERVICE_ID")
    private int serviceId;
    private String username;
}
