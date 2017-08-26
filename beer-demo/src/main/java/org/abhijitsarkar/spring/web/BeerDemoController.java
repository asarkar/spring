package org.abhijitsarkar.spring.web;

import lombok.RequiredArgsConstructor;
import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.domain.Brewery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.couchbase.client.deps.io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequiredArgsConstructor
public class BeerDemoController {
    private final List<CrudRepository<Beer, String>> beerRepositories;
    private final List<CrudRepository<Brewery, String>> breweryRepositories;

    @GetMapping(path = "/beers/{id}", produces = APPLICATION_JSON)
    public ResponseEntity<Beer> beer(@PathVariable("id") String id) {
        return findOne(id, beerRepositories);
    }

    @GetMapping(path = "/breweries/{id}", produces = APPLICATION_JSON)
    public ResponseEntity<Brewery> brewery(@PathVariable("id") String id) {
        return findOne(id, breweryRepositories);
    }

    private <T> ResponseEntity<T> findOne(String id, List<CrudRepository<T, String>> repositories) {
        return repositories.stream()
                .findAny()
                .map(repo -> repo.findOne(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
