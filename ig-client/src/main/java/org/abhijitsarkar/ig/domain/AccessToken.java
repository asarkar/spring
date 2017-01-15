package org.abhijitsarkar.ig.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Abhijit Sarkar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AccessToken {
    @JsonProperty("access_token")
    private String token;
}
