package name.abhijitsarkar.javaee.userpref.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_PREFS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceEntity {
    @EmbeddedId
    private UserPreferenceId id;

    @Column(name = "VAL")
    private String value;
}
