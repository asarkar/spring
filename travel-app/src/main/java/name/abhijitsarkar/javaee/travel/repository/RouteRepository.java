package name.abhijitsarkar.javaee.travel.repository;

import name.abhijitsarkar.javaee.travel.domain.Page;
import name.abhijitsarkar.javaee.travel.domain.Route;
import rx.Observable;

import java.time.DayOfWeek;

public interface RouteRepository {
    Observable<Page<Route>> findRoutes(String srcAirportFaa, String destAirportFaa,
                                       DayOfWeek departureDay, int pageSize, int pageNum);
}