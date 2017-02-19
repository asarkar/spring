package org.abhijitsarkar.ufo.service;

import org.abhijitsarkar.ufo.domain.ProducerProperties;
import org.abhijitsarkar.ufo.domain.Sighting;
import org.abhijitsarkar.ufo.repository.Crawler;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.YearMonth;
import java.util.List;

import static java.time.temporal.ChronoUnit.MONTHS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Abhijit Sarkar
 */
public class ProducerTest {
    private Crawler crawler;

    @Before
    public void setup() {
        crawler = mock(Crawler.class);

        when(crawler.getSightings(any(YearMonth.class), any(YearMonth.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    YearMonth from = (YearMonth) args[0];
                    YearMonth to = (YearMonth) args[1];

                    if (from == null || to == null) {
                        return Flux.empty();
                    }

                    System.out.println(String.format("From: %s, to: %s.", from, to));

                    if (from.equals(YearMonth.of(2011, 5))
                            && to.equals(YearMonth.of(2011, 12))
                            || from.equals(YearMonth.of(2012, 1))
                            && to.equals(YearMonth.of(2012, 12))
                            || from.equals(YearMonth.of(2013, 1))
                            && to.equals(YearMonth.of(2013, 11))
                            ) {
                        int months = (int) from.until(to, MONTHS) + 1;
                        return Flux.range(1, months).map(i -> stubSighting(i));
                    }
                    return Flux.empty();
                });
    }

    @Test
    public void testGetSightings() {
        Producer producer = new Producer(null, producerProperties(), this.crawler);

        List<Sighting> sightings = producer.getSightings()
                .collectList()
                .block();

        assertThat(sightings, hasSize(31));
    }

    private Sighting stubSighting(int month) {
        return Sighting.builder()
                .summary(Integer.toString(month))
                .build();
    }

    private ProducerProperties producerProperties() {
        ProducerProperties producerProperties = new ProducerProperties();
        producerProperties.setFromYearMonth("2011-05");
        producerProperties.setToYearMonth("2013-11");
        producerProperties.setDelayMillis(1000l);

        return producerProperties;
    }
}