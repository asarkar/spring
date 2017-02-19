package org.abhijitsarkar.ufo.service;

import org.abhijitsarkar.ufo.domain.Sighting;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Abhijit Sarkar
 */
public class ConsumerTest {
    @Test
    public void testGetAnalytics() {
        List<Sighting> sightings = IntStream.range(1, 13)
                .mapToObj(this::stubSighting)
                .collect(toList());
        Consumer consumer = new Consumer();
        consumer.listen(sightings);

        Map<String, Map<String, Integer>> analytics = consumer.getAnalytics();

        assertThat(analytics.get("state"), is(notNullValue()));
        assertThat(analytics.get("state").size(), is(1));
        assertThat(analytics.get("shape"), is(notNullValue()));
        assertThat(analytics.get("shape").size(), is(1));
        assertThat(analytics.get("year"), is(notNullValue()));
        assertThat(analytics.get("year").size(), is(1));
        assertThat(analytics.get("month"), is(notNullValue()));
        assertThat(analytics.get("month").size(), is(12));
    }

    private Sighting stubSighting(int month) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(2);

        return Sighting.builder()
                .state("wa")
                .shape("round")
                .eventDateTime(String.format("2017-%s-01 00:00", numberFormat.format(month)))
                .build();
    }
}