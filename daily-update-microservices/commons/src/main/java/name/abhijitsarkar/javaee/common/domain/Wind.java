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
public class Wind implements Serializable {
    private static final long serialVersionUID = 3519964670278295565L;

    private double speed;
    private double degree;
}
