package org.abhijitsarkar.spring.domain;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

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
    private String updated;
    @Column(length = 999)
    private String description;
    @ElementCollection
    private List<String> address;
    @Embedded
    private Geo geo;

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