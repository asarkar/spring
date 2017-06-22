package org.abhijitsarkar.licensereport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private int id;
    private String name;
    @JsonProperty("web_url")
    private String url;
    private List<Project> projects;
}
