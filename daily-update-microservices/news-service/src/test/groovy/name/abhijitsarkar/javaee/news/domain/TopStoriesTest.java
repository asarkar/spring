package name.abhijitsarkar.javaee.news.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import name.abhijitsarkar.javaee.common.domain.Story;
import org.junit.Test;

import java.io.InputStream;
import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Abhijit Sarkar
 */
public class TopStoriesTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

    @Test
    public void testGetStories() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/top-stories.json")) {
            ObjectReader reader = objectMapper.reader();

            Story topScienceStory = reader.forType(TopStories.class).<TopStories>readValue(is).
                    getStories().stream().filter(s -> "Science".equals(s.getSection())).findFirst().get();

            assertEquals("Science", topScienceStory.getSection());
            assertTrue(topScienceStory.getSubsection().isEmpty());
            assertEquals("December Heat Tricks Spring Flowers Into Bloom", topScienceStory.getTitle());
            assertFalse(topScienceStory.getSummary().isEmpty());
            assertNotNull(topScienceStory.getUrl());
            assertEquals("By NICHOLAS ST. FLEUR", topScienceStory.getReporter());

            OffsetDateTime publishedDateTime = topScienceStory.getPublishedDateTime();

            assertNotNull(publishedDateTime);

            String formattedPublishedDateTime = ISO_OFFSET_DATE_TIME.format(publishedDateTime);

            assertEquals("2015-12-25T00:00:00-05:00", formattedPublishedDateTime);
        }
    }
}