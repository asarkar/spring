package name.abhijitsarkar.javaee.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.net.URL;
import java.time.OffsetDateTime;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Story implements Serializable {
    private static final long serialVersionUID = 5496206484601333899L;

    private String section;
    private String subsection;
    private String title;
    @JsonProperty("abstract")
    private String summary;
    @JsonProperty("byline")
    private String reporter;
    private URL url;
    @JsonProperty("published_date")
    @JsonDeserialize(using = PublishedDateTimeDeserializer.class)
    private OffsetDateTime publishedDateTime;
}
