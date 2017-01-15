package name.abhijitsarkar.javaee.travel.repository;

import name.abhijitsarkar.javaee.travel.domain.Airport;
import name.abhijitsarkar.javaee.travel.domain.Page;
import rx.Observable;

import java.util.Collection;
import java.util.List;

public interface AirportRepository {
    Observable<Collection<Airport>> findByFaaCodesIn(List<String> faaCodes);

    Observable<Collection<Airport>> findAirports(String searchTerm);
}