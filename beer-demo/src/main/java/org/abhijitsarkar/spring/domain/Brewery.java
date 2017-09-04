package org.abhijitsarkar.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Abhijit Sarkar
 */
@Data
public class Brewery {
    private String name;
    private String city;
    private String state;
    private String code;
    private String country;
    private String phone;
    private String website;
    private String type;
    @JsonIgnore
    private String updated;
    private String description;
    private List<String> address;
    private Geo geo;

    @JsonProperty("updated")
    private String getUpdated() {
        return OffsetDateTime.now().format(ISO_OFFSET_DATE_TIME);
    }

    private void setUpdated(String updated) {
        this.updated = updated;
    }

    @Data
    static class Geo {
        private String accuracy;
        @JsonProperty("lat")
        private Double latitude;
        @JsonProperty("lon")
        private Double longitude;
    }
}