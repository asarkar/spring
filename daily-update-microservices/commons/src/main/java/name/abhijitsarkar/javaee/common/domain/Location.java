package name.abhijitsarkar.javaee.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Abhijit Sarkar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Serializable {
    private static final long serialVersionUID = 7053867798857026369L;

    private double latitude;
    private double longitude;
    private String name;
    private String countryCode;
}
