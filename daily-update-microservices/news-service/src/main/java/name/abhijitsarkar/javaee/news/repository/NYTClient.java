package name.abhijitsarkar.javaee.news.repository;

import name.abhijitsarkar.javaee.news.domain.TopStories;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@FeignClient(name = "nyt",
        url = "${nyt.base-url}?api-key=${nyt.top-stories.api-key}")
@CacheConfig(cacheResolver = "cacheResolver")
public interface NYTClient {
    @RequestMapping(value = "svc/topstories/v2/{section}.json",
            method = GET, produces = "text/json")
    @Cacheable(keyGenerator = "cacheKeyGenerator")
    public TopStories getTopStories(@PathVariable("section") String section);
}
