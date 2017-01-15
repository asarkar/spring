package name.abhijitsarkar.javaee.news.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import name.abhijitsarkar.javaee.common.domain.Story;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Abhijit Sarkar
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopStories implements Serializable {
    private static final long serialVersionUID = -1612462533676067037L;

    @JsonProperty("results")
    Collection<Story> stories;
}
