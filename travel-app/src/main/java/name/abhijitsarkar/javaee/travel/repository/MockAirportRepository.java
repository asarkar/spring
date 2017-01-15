package name.abhijitsarkar.javaee.travel.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.abhijitsarkar.javaee.travel.domain.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Abhijit Sarkar
 */
@Repository
@Profile("noDB")
public class MockAirportRepository implements AirportRepository {
    @Autowired
    private ObjectMapper objectMapper;

    private Collection<Airport> airports;

    @PostConstruct
    void initAirports() throws IOException {
        TypeReference<List<Airport>> typeReference = new TypeReference<List<Airport>>() {
        };

        try (InputStream is = getClass().getResourceAsStream("/airports.json")) {
            airports = objectMapper.reader().forType(typeReference).readValue(is);
        }
    }

    @Override
    public Observable<Collection<Airport>> findByFaaCodesIn(List<String> faaCodes) {
        return Observable.just(airports);
    }

    @Override
    public Observable<Collection<Airport>> findAirports(String searchTerm) {
        return Observable.just(airports);
    }
}
