package org.abhijitsarkar.ufo.repository;

import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.ufo.domain.Sighting;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import static org.jsoup.helper.HttpConnection.connect;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@Component
public class CrawlerImpl implements Crawler {
    private static final String BASE_URL = "http://www.nuforc.org/webreports";

    @Override
    public Flux<Sighting> getSightings(YearMonth from, YearMonth to) {
        int months = (int) from.until(to, ChronoUnit.MONTHS) + 1;

        return Flux.range(0, months)
                .map(from::plusMonths)
                .flatMap(this::getSightings);
    }

    @Override
    public Flux<Sighting> getSightings(YearMonth yearMonth) {
        String url = String.format("%s/ndxe%s.html", BASE_URL, yearMonth.format(YEAR_MONTH_FORMATTER));
        return Mono.fromCallable(() -> connect(url).timeout(5000).get())
                .map(d -> d.select("table").first())
                .flatMap(t -> Flux.fromIterable(t.select("tr")))
                .flatMap(r -> {
                    Elements col = r.select("td");

                    if (col.size() < 6) {
                        return Flux.empty();
                    }
                    return Mono.just(
                            Sighting.builder()
                                    .eventDateTime(col.get(0).text(), yearMonth)
                                    .city(col.get(1).text())
                                    .state(col.get(2).text())
                                    .shape(col.get(3).text())
                                    .duration(col.get(4).text())
                                    .summary(col.get(5).text())
                                    .build()
                    );
                })
                .onErrorResumeWith(t -> {
                    log.error("Failed to crawl: {}.", url, t);
                    return Flux.empty();
                });
    }
}
