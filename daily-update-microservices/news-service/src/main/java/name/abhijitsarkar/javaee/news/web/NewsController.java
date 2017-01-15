package name.abhijitsarkar.javaee.news.web;

import name.abhijitsarkar.javaee.common.domain.Story;
import name.abhijitsarkar.javaee.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(value = "news",
        method = GET, produces = APPLICATION_JSON_VALUE)
public class NewsController {
    @Autowired
    private NewsService newsService;

    @RequestMapping("topstories")
    public Collection<Story> getTopStories(
            @RequestParam(value = "sections", required = false)
            Collection<String> sections) {
        return newsService.getTopStories(sections);
    }
}
