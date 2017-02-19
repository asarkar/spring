package org.abhijitsarkar.ufo.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * @author Abhijit Sarkar
 */
public class SightingSerializer extends JsonSerializer<Sighting> {
    public SightingSerializer() {
        super(getObjectMapper());
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}
