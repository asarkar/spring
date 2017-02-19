package org.abhijitsarkar.ufo.repository;

import org.abhijitsarkar.ufo.domain.Sighting;
import reactor.core.publisher.Flux;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * @author Abhijit Sarkar
 */
public interface Crawler {
    DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    Flux<Sighting> getSightings(YearMonth from, YearMonth to);

    Flux<Sighting> getSightings(YearMonth yearMonth);
}
