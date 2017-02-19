package org.abhijitsarkar.ufo.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * @author Abhijit Sarkar
 */
public class SightingDeserializer extends JsonDeserializer<Sighting> {
    public SightingDeserializer() {
        super(getObjectMapper());
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}
