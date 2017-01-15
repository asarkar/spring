package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.query.Statement;
import name.abhijitsarkar.javaee.travel.domain.Page;
import name.abhijitsarkar.javaee.travel.domain.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import rx.Observable;

import java.time.DayOfWeek;
import java.util.function.Function;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.Sort.asc;

/**
 * @author Abhijit Sarkar
 */
@Repository
@Profile("!noDB")
public class RouteRepositoryImpl implements RouteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRepositoryImpl.class);

    static final String FIELD_SRC_AIRPORT_FAA = "srcAirportFaa";
    static final String FIELD_DEST_AIRPORT_FAA = "destAirportFaa";
    static final String FIELD_AIRLINE = "airline";
    static final String FIELD_FLIGHT_NUM = "flightNum";
    static final String FIELD_AIRCRAFT = "aircraft";
    static final String FIELD_STOPS = "stops";
    static final String FIELD_DEPARTURE_TIME = "departureTimeUTC";
    static final String FIELD_DEPARTURE_DAY = "departureDay";

    @Autowired
    private Bucket bucket;

    Function<N1qlQueryRow, Route> mapper = new N1qlQueryRowToRouteMapper();

    private Statement findRoutes(int limit, int offset) {
        return select(
                path("route", "sourceairport").as(FIELD_SRC_AIRPORT_FAA),
                path("route", "destinationairport").as(FIELD_DEST_AIRPORT_FAA),
                path("route", "stops").as(FIELD_STOPS),
                path("route", "equipment").as(FIELD_AIRCRAFT),
                path("airln", "name").as(FIELD_AIRLINE),
                path("schedule", "flight").as(FIELD_FLIGHT_NUM),
                path("schedule", "utc").as(FIELD_DEPARTURE_TIME),
                path("schedule", "day").as(FIELD_DEPARTURE_DAY)
        )
                .from(i(bucket.name()).as(x("route")))
                .unnest(path("route", "schedule").as(x("schedule")))
                .join(i(bucket.name()).as(x("airln")))
                .onKeys(path("route", "airlineid"))
                .where(path("route", "sourceairport").eq(x("$srcAirportFaa"))
                        .and(path("route", "destinationairport").eq(x("$destAirportFaa")))
                        .and(path("schedule", "day").eq(x("$departureDay"))))
                .orderBy(asc(path("airln", "name"))).limit(limit).offset(offset);
    }

    /*
     * Every time a Subscriber subscribes, the call() method is executed (implemented as a lambda expression here).
     * We can then call onNext, onComplete and onError as you wish, but must keep in mind that both onComplete and
     * onError should only be called once, and afterward no subsequent onNext is allowed to follow.
     *
     * No blocking call is needed, because the observable is completely handled on the current thread.
     */
    public Observable<Page<Route>> findRoutes(String srcAirportFaa, String destAirportFaa,
                                              DayOfWeek departureDay, int pageSize, int pageNum) {
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(
                findRoutes(pageSize, (pageNum - 1) * pageSize),
                JsonObject.create()
                        .put(FIELD_SRC_AIRPORT_FAA, srcAirportFaa)
                        .put(FIELD_DEST_AIRPORT_FAA, destAirportFaa)
                            /* DayOfWeek is 1-based, field in document is 0-based */
                        .put(FIELD_DEPARTURE_DAY, departureDay.getValue() - 1));

        GenericPagingSubscriber subscriber = GenericPagingSubscriber.<Route>builder()
                .mapper(mapper)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .bucket(bucket)
                .query(query)
                .build();

        return Observable.create(subscriber);
    }
}
