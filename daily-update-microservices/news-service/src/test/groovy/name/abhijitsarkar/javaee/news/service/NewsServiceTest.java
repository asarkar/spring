package name.abhijitsarkar.javaee.news.service;

import com.google.common.collect.ImmutableList;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import name.abhijitsarkar.javaee.common.domain.Story;
import name.abhijitsarkar.javaee.news.repository.NYTClient;
import name.abhijitsarkar.javaee.news.repository.NYTClientStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Abhijit Sarkar
 */
@RunWith(JMockit.class)
public class NewsServiceTest {
    private NewsService newsService = new NewsService();
    NYTClientStub nytClientStub = new NYTClientStub();

    @Mocked
    private NYTClient nytClient;

    @Before
    public void before() {
        newsService.nytClient = nytClient;
    }

    @Test
    public void testGetTopStoriesInWorld() {
        new Expectations() {{
            nytClient.getTopStories("world");
            result = nytClientStub.getTopStories("world");
        }};

        Collection<Story> topStories = newsService.getTopStories(ImmutableList.of("world"));

        assertFalse(isEmpty(topStories));

        topStories.stream().forEach(s -> assertEquals("world", s.getSection().toLowerCase()));
    }

    @Test
    public void testGetTopStoriesCaseInsensitive() {
        new Expectations() {{
            nytClient.getTopStories("world");
            result = nytClientStub.getTopStories("world");
        }};

        Collection<Story> topStories = newsService.getTopStories(ImmutableList.of("WORLD"));

        assertFalse(isEmpty(topStories));

        topStories.stream().forEach(s -> assertEquals("world", s.getSection().toLowerCase()));
    }

    @Test
    public void testGetTopStoriesForNonExistentSection() {
        new Expectations() {{
            nytClient.getTopStories("junk");
            result = nytClientStub.getTopStories("junk");
        }};

        Collection<Story> topStories = newsService.getTopStories(ImmutableList.of("junk"));

        assertTrue(isEmpty(topStories));
    }
}

