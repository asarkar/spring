package org.abhijitsarkar.ufo.repository;

import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;

/**
 * @author Abhijit Sarkar
 */
public class CrawlerImplTest {
    @Test
    public void testGetIncidents() {
        YearMonth from = YearMonth.of(2017, 01);
        YearMonth to = YearMonth.of(2017, 02);

        StepVerifier.create(new CrawlerImpl().getSightings(from, to))
                .expectNextMatches(s -> {
                    LocalDateTime eventDateTime = s.getEventDateTime();
                    return eventDateTime.getYear() == 2017
                            && (eventDateTime.getMonth().equals(Month.JANUARY)
                            || eventDateTime.getMonth().equals(Month.FEBRUARY));
                })
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }
}