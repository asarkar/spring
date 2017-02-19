package org.abhijitsarkar.ufo.domain;

import org.junit.Test;

import java.time.YearMonth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Abhijit Sarkar
 */
public class SightingSerializerDeserializerTest {
    @Test
    public void testSerDeser() {
        byte[] ser = new SightingSerializer().serialize("test", stubSighting());
        Sighting sighting = new SightingDeserializer().deserialize("test", ser);

        assertThat(sighting, is(notNullValue()));
        assertThat(sighting.getState(), is("wa"));
        assertThat(sighting.getShape(), is("round"));
        assertThat(sighting.getEventDateTime(), is(notNullValue()));
        assertThat(sighting.getEventDateTime().getMonthValue(), is(1));
        assertThat(sighting.getEventDateTime().getYear(), is(2017));
        assertThat(sighting.getDuration(), is(notNullValue()));
        assertThat(sighting.getDuration().getSeconds(), is(600l));
    }

    private Sighting stubSighting() {
        return Sighting.builder()
                .state("wa")
                .shape("round")
                .eventDateTime("1/1/17", YearMonth.of(2017, 1))
                .duration("10 minutes")
                .build();
    }
}
