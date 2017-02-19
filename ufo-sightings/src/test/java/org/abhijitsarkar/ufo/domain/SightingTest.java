package org.abhijitsarkar.ufo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Abhijit Sarkar
 */
public class SightingTest {
    @Test
    public void testParseDuration() {
        List<Long> results = Stream.of("5-6 minutes", "6 minutes", "1 to 6 minutes", "~6 minutes")
                .map(i -> Sighting.builder()
                        .duration(i)
                        .build()
                        .getDuration()
                        .getSeconds())
                .collect(toList());

        assertThat(results, everyItem(is(360l)));
    }

    @Test
    public void testParseDurationDefault() {
        long seconds = Sighting.builder()
                .duration("junk")
                .build()
                .getDuration()
                .getSeconds();

        assertThat(seconds, is(-1l));
    }

    @Test
    public void testParseEventDateTime() {
        LocalDateTime eventDateTime = Sighting.builder()
                .eventDateTime("2/9/17", YearMonth.of(2017, 02))
                .build()
                .getEventDateTime();

        assertThat(eventDateTime.getYear(), is(2017));
        assertThat(eventDateTime.getMonth(), is(Month.FEBRUARY));
        assertThat(eventDateTime.getDayOfMonth(), is(9));

        eventDateTime = Sighting.builder()
                .eventDateTime("2/9/17 13:05", YearMonth.of(2017, 02))
                .build()
                .getEventDateTime();

        assertThat(eventDateTime.getYear(), is(2017));
        assertThat(eventDateTime.getMonth(), is(Month.FEBRUARY));
        assertThat(eventDateTime.getDayOfMonth(), is(9));
        assertThat(eventDateTime.getHour(), is(13));
        assertThat(eventDateTime.getMinute(), is(5));
    }

    @Test
    public void testParseEventDateTimeDefault() {
        LocalDateTime eventDateTime = Sighting.builder()
                .eventDateTime("junk", YearMonth.of(2017, 02))
                .build()
                .getEventDateTime();

        assertThat(eventDateTime.getYear(), is(1));
        assertThat(eventDateTime.getMonth(), is(Month.JANUARY));
        assertThat(eventDateTime.getDayOfMonth(), is(1));
        assertThat(eventDateTime.getHour(), is(0));
        assertThat(eventDateTime.getMinute(), is(0));
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        Sighting sighting = Sighting.builder()
                .eventDateTime("2/9/17 13:05", YearMonth.of(2017, 02))
                .duration("10 minutes")
                .build();

        String str = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writer()
                .writeValueAsString(sighting);

        assertThat(str, is("{\"eventDateTime\":\"2017-02-09 13:05\",\"shape\":null,\"state\":null,\"city\":null,\"duration\":\"10 minutes\",\"summary\":null}"));
    }
}