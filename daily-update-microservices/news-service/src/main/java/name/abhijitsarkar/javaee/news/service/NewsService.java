package name.abhijitsarkar.javaee.news.service;

import name.abhijitsarkar.javaee.common.domain.Story;
import name.abhijitsarkar.javaee.news.repository.NYTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Abhijit Sarkar
 */
@Service
public class NewsService {
    @Autowired
    NYTClient nytClient;

    public Collection<Story> getTopStories(final Collection<String> sections) {
        Collection<String> lowercaseSections = sections.stream().map(s -> s.trim().toLowerCase()).collect(toList());

        String section = isEmpty(lowercaseSections) ? "world" : lowercaseSections.iterator().next();

        Collection<Story> topStories = nytClient.getTopStories(section).getStories();

        return topStories.stream().filter(s -> lowercaseSections.contains(s.getSection().toLowerCase())).collect(toList());
    }
}
