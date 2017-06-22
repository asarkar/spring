package org.abhijitsarkar.licensereport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    @JsonProperty("ssh_url_to_repo")
    private String sshUrl;
    private String name;
}
