package name.abhijitsarkar.javaee.userpref.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {
    private String name;
    private String value;
    private int serviceId;
}
