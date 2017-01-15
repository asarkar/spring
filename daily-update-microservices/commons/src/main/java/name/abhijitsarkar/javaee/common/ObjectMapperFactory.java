package name.abhijitsarkar.javaee.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Abhijit Sarkar
 */
public class ObjectMapperFactory {
    public static ObjectMapper newInstance() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
                .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());

//        mapper.setVisibility(FIELD, JsonAutoDetect.Visibility.NONE)
//                .setVisibility(GETTER, JsonAutoDetect.Visibility.ANY)
//                .setVisibility(SETTER, JsonAutoDetect.Visibility.ANY)
//                .setVisibility(CREATOR, JsonAutoDetect.Visibility.NONE);

        return mapper;
    }
}
