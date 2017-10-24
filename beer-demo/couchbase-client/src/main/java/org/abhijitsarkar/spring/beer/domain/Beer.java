package org.abhijitsarkar.spring.beer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Abhijit Sarkar
 */
@Data
public class Beer {
    private String name;
    private Float abv;
    private Float ibu;
    private Float srm;
    private Float upc;
    private String type;
    @JsonIgnore
    private Brewery brewery;
    @JsonIgnore
    private String updated;
    private String description;
    private String style;
    private String category;

    @JsonProperty("updated")
    private String getUpdated() {
        return OffsetDateTime.now().format(ISO_OFFSET_DATE_TIME);
    }

    private void setUpdated(String updated) {
        this.updated = updated;
    }

    @JsonProperty("brewery_id")
    public String getBrewery() {
        return brewery.getName();
    }

    public void setBrewery(String name) {
        brewery = new Brewery();
        brewery.setName(name);
    }
}
