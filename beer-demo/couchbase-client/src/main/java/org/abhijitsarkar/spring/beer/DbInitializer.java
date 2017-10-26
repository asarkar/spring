package org.abhijitsarkar.spring.beer;

import com.couchbase.client.java.document.RawJsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.beer.domain.Beer;
import org.abhijitsarkar.spring.beer.domain.Brewery;
import org.abhijitsarkar.spring.beer.factory.CouchbaseAsyncBucketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class DbInitializer {
    private static final long DELAY = 30L;

    private final CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory;
    private final AtomicBoolean ranOnce = new AtomicBoolean();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${beer-demo.initialize:false}")
    private boolean initialize;

    @EventListener
    // Spring event handling is blocking by default; this is the simplest way to handle events on a separate thread on a
    // case-by-case basis; using multicast makes every event handler async.
    @Async
    void doWhenApplicationIsReady(ApplicationReadyEvent event) {
        if (initialize == false) {
            return;
        }

        Observable.timer(DELAY, TimeUnit.SECONDS)
                // zip will unsubscribe on error, so use flatMap
                .flatMap(i -> save())
                .doOnError(t -> log.error("Failed to initialize database.", t))
                .toList()
                .doOnNext(l -> {
                    boolean initialized = ranOnce.compareAndSet(false, true);
                    log.info("Database: {} initialized.",
                            initialized ? "successfully" : "cannot be");
                })
                .retryWhen(errors -> errors.filter(e -> !ranOnce.get()))
                .toCompletable()
                .await();
    }

    private Observable<RawJsonDocument> save() {
        log.info("Initializing database");

        try (InputStream breweries = new ClassPathResource("/breweries.json").getInputStream()) {
            List<Brewery> list1 = objectMapper.readValue(breweries, new TypeReference<List<Brewery>>() {
            });

            return save(toJsonDocuments(list1, Brewery::getName, objectMapper))
                    .flatMap(x -> {
                        try (InputStream beers = new ClassPathResource("/beers.json").getInputStream()) {
                            List<Beer> list2 = objectMapper.readValue(beers, new TypeReference<List<Beer>>() {
                            });

                            return save(toJsonDocuments(list2, Beer::getName, objectMapper));
                        } catch (IOException e) {
                            return Observable.error(e);
                        }
                    });
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private Observable<RawJsonDocument> save(List<RawJsonDocument> list) {
        return couchbaseAsyncBucketFactory.getAsyncBucketInstance()
                .flatMapObservable(bucket -> list
                        .stream()
                        .map(bucket::upsert)
                        .collect(collectingAndThen(toList(), Observable::from)))
                .flatMap(obs -> obs)
                .doOnNext(doc -> log.debug("Successfully saved doc with id: {}.", doc.id()));
    }

    private <T> List<RawJsonDocument> toJsonDocuments(
            Collection<T> list,
            Function<T, String> idMapper,
            ObjectMapper objectMapper
    ) {
        return list.stream()
                .map(element -> {
                    try {
                        return RawJsonDocument.create(
                                idMapper.apply(element),
                                objectMapper.writeValueAsString(element)
                        );
                    } catch (JsonProcessingException e) {
                        log.error("Failed to convert element: {} to JsonStringDocument.", element, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
