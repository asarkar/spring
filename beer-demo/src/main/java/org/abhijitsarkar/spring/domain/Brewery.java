package org.abhijitsarkar.spring.domain;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Abhijit Sarkar
 */
@Entity
@Table(name = "brewery")
@Data
public class Brewery {
    @javax.persistence.Id
    @org.springframework.data.annotation.Id
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
    @Column(length = 999)
    private String description;
    @ElementCollection
    private List<String> address;
    @Embedded
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
        @Field("lat")
        @Column(name = "lat")
        @JsonProperty("lat")
        private Double latitude;
        @Field("lon")
        @Column(name = "lon")
        @JsonProperty("lon")
        private Double longitude;
    }
}