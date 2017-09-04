package org.abhijitsarkar.spring.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.domain.Brewery;
import org.abhijitsarkar.spring.repository.CouchbaseBeerRepository;
import org.abhijitsarkar.spring.repository.CouchbaseBreweryRepository;
import org.abhijitsarkar.spring.repository.CouchbaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class BeerDemoController {
    private static final long TIMEOUT_MILLIS = 3000L;

    private final CouchbaseBeerRepository beerRepository;
    private final CouchbaseBreweryRepository breweryRepository;

    @GetMapping(path = "/beers/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Beer> beer(@PathVariable("id") String id) {
        return beerRepository.findOne(id)
                .map(ResponseEntity::ok)
                .onErrorReturn(t -> {
                    if (t instanceof NoSuchElementException) {
                        return ResponseEntity.notFound().build();
                    } else {
                        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
                    }
                })
                .timeout(TIMEOUT_MILLIS, MILLISECONDS)
                .toBlocking()
                .value();
    }

    @GetMapping(path = "/breweries/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Brewery> brewery(@PathVariable("id") String id) {
        return breweryRepository.findAll(asList(id), "type", "brewery")
                .first()
                .map(ResponseEntity::ok)
                .onErrorReturn(t -> ResponseEntity.status(INTERNAL_SERVER_ERROR).build())
                .switchIfEmpty(Observable.just(ResponseEntity.notFound().build()))
                .timeout(TIMEOUT_MILLIS, MILLISECONDS)
                .toBlocking()
                .single();
    }

    @GetMapping(path = "/beers", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Beer>> beers() {
        return findAll(beerRepository, "beer");
    }

    @GetMapping(path = "/breweries", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Brewery>> breweries() {
        return findAll(breweryRepository, "brewery");
    }

    private <T> ResponseEntity<List<T>> findAll(CouchbaseRepository<T> repository, String type) {
        return repository.findAll("type", type)
                .toList()
                .map(ResponseEntity::ok)
                .onErrorReturn(t -> ResponseEntity.status(INTERNAL_SERVER_ERROR).build())
                .switchIfEmpty(Observable.just(ResponseEntity.notFound().build()))
                .timeout(TIMEOUT_MILLIS, MILLISECONDS)
                .toBlocking()
                .single();
    }
}
