package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.path.AsPath;
import name.abhijitsarkar.javaee.travel.domain.Airport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

/**
 * @author Abhijit Sarkar
 */
@Repository
@Profile("!noDB")
public class AirportRepositoryImpl implements AirportRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirportRepositoryImpl.class);

    static final String FIELD_NAME = "airportname";
    static final String FIELD_FAA = "faa";
    static final String FIELD_ICAO = "icao";
    static final String FIELD_CITY = "city";
    static final String FIELD_COUNTRY = "country";
    static final String FIELD_TIMEZONE = "tz";
    static final String FIELD_TYPE = "type";
    static final String VAL_TYPE = "airport";

    private static final String PARAM_FAA = "$faaCode";
    private static final String PARAM_ICAO = "$icaoCode";
    private static final String PARAM_NAME = "$name";
    private static final String PARAM_CITY = "$city";

    private Statement findByFaaCodes;
    private AsPath findAll;

    @Autowired
    private Bucket bucket;

    Function<N1qlQueryRow, Airport> mapper = new N1qlQueryRowToAirportMapper();

    @PostConstruct
    void postConstruct() {
        findAll = select(path(VAL_TYPE, "*"))
                .from(i(bucket.name()).as(x(VAL_TYPE)));

        findByFaaCodes = findAll
                .where(x(FIELD_FAA).in(x(PARAM_FAA))
                        .and(x(FIELD_TYPE).eq(s(VAL_TYPE))));
    }

    /*
     * Every time a Subscriber subscribes, the call() method is executed (implemented as a lambda expression here).
     * We can then call onNext, onComplete and onError as you wish, but must keep in mind that both onComplete and
     * onError should only be called once, and afterward no subsequent onNext is allowed to follow.
     *
     * No blocking call is needed, because the observable is completely handled on the current thread.
     */
    @Override
    public Observable<Collection<Airport>> findByFaaCodesIn(List<String> faaCodes) {
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(findByFaaCodes,
                JsonObject.create().put(PARAM_FAA, JsonArray.from(faaCodes)));

        return Observable.create(new GenericSubscriber<Airport>(query, bucket, mapper));
    }

    @Override
    public Observable<Collection<Airport>> findAirports(String searchTerm) {
        String lowerCasedSearchTerm = searchTerm.toLowerCase();

        N1qlQuery query = N1qlQuery.simple(String.format(
                "SELECT airport.* " +
                        "FROM `%s` AS airport " +
                        "WHERE airport.type = '%s' " +
                        "AND (LOWER(airport.faa) LIKE '%s%%' " +
                        "OR LOWER(airport.icaoCode) LIKE '%s%%' " +
                        "OR LOWER(airport.airportname) LIKE '%s%%' " +
                        "OR LOWER(airport.city) LIKE '%s%%') " +
                        "ORDER BY airport.city ASC, airport.country ASC;",
                bucket.name(), VAL_TYPE, lowerCasedSearchTerm, lowerCasedSearchTerm,
                lowerCasedSearchTerm, lowerCasedSearchTerm));

        GenericSubscriber subscriber = GenericSubscriber.<Airport>builder()
                .mapper(mapper)
                .bucket(bucket)
                .query(query)
                .build();

        return Observable.create(subscriber);
    }
}
