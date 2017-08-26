package org.abhijitsarkar.spring.domain;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Abhijit Sarkar
 */
@Document
@Entity
@Table(name = "beer")
@Data
public class Beer {
    @javax.persistence.Id
    @org.springframework.data.annotation.Id
    private String name;
    private Float abv;
    private Float ibu;
    private Float srm;
    private Float upc;
    private String type;
    @Field("brewery_id")
    @ManyToOne
    @JoinColumn(name = "brewery_id")
    @JsonIgnore
    private Brewery brewery;
    private String updated;
    @Column(length = 999)
    private String description;
    private String style;
    private String category;

    @JsonProperty("brewery_id")
    public String getBrewery() {
        return brewery.getName();
    }

    public void setBrewery(String name) {
        brewery = new Brewery();
        brewery.setName(name);
    }
}
