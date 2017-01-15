package name.abhijitsarkar.javaee.dailyupdate.repository;

import name.abhijitsarkar.javaee.common.domain.Story;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "news-service")
public interface NewsServiceClient {
    @RequestMapping(value = "news/topstories", method = GET, produces = APPLICATION_JSON_VALUE)
    public Collection<Story> getTopStories(
            @RequestParam(value = "sections", required = false) Collection<String> sections);
}
