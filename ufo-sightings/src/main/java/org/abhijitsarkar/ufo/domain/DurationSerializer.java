package org.abhijitsarkar.ufo.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

/**
 * @author Abhijit Sarkar
 */
public class DurationSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration duration, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (duration == null) {
            gen.writeNull();
            return;
        }
        long hours = duration.toHours();

        if (hours > 0) {
            gen.writeString(hours + " hours");
        } else {
            long minutes = duration.toMinutes();

            if (minutes > 0) {
                gen.writeString(minutes + " minutes");
            } else {
                gen.writeString(duration.getSeconds() + " seconds");
            }
        }
    }
}
