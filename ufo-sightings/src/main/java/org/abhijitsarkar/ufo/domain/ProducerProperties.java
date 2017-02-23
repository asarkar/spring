package org.abhijitsarkar.ufo.domain;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

import static java.time.Month.JANUARY;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("sighting.producer")
@Component
public class ProducerProperties {
    @Setter
    private Long delayMillis;
    @Setter
    private String fromYearMonth;
    @Setter
    private String toYearMonth;

    public YearMonth getFromYearMonth() {
        return fromYearMonth == null ? YearMonth.now().withMonth(JANUARY.getValue()) : YearMonth.parse(fromYearMonth);
    }

    public YearMonth getToYearMonth() {
        return toYearMonth == null ? YearMonth.now() : YearMonth.parse(toYearMonth);
    }

    public long getDelayMillis() {
        return delayMillis == null ? 5000 : delayMillis;
    }
}
