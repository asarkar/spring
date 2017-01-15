package name.abhijitsarkar.javaee.news.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import name.abhijitsarkar.javaee.common.ObjectMapperFactory;
import name.abhijitsarkar.javaee.news.domain.TopStories;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class NYTClientStub implements NYTClient {
    private ObjectMapper objectMapper = ObjectMapperFactory.newInstance();

    @Override
    public TopStories getTopStories(String section) {
        TopStories topStories = null;

        try (InputStream is = getClass().getResourceAsStream("/top-stories.json")) {
            ObjectReader reader = objectMapper.reader();

            topStories = reader.forType(TopStories.class).<TopStories>readValue(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return topStories;
    }
}
