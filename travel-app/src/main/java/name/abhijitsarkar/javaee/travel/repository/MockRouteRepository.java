package name.abhijitsarkar.javaee.travel.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.abhijitsarkar.javaee.travel.domain.Page;
import name.abhijitsarkar.javaee.travel.domain.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;

/**
 * @author Abhijit Sarkar
 */
@Repository
@Profile("noDB")
public class MockRouteRepository implements RouteRepository {
    @Autowired
    private ObjectMapper objectMapper;

    private Collection<Route> routes;

    @PostConstruct
    void initRoutes() throws IOException {
        TypeReference<List<Route>> typeReference = new TypeReference<List<Route>>() {
        };

        try (InputStream is = getClass().getResourceAsStream("/routes.json")) {
            routes = objectMapper.reader().forType(typeReference).readValue(is);
        }
    }

    @Override
    public Observable<Page<Route>> findRoutes(String srcAirportFaa, String destAirportFaa,
                                              DayOfWeek departureDay, int pageSize, int pageNum) {
        return Observable.just(new Page<Route>(1, routes.size(), 1, routes));
    }
}
