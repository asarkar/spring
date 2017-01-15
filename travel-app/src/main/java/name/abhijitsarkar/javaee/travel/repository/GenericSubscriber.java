package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.util.Collection;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
@Builder
public class GenericSubscriber<T> implements Observable.OnSubscribe<Collection<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSubscriber.class);
    private final N1qlQuery query;
    private final Bucket bucket;
    private final Function<N1qlQueryRow, T> mapper;

    @Override
    public void call(Subscriber<? super Collection<T>> subscriber) {
        LOGGER.debug("Executing query: {}.", query.n1ql());

        N1qlQueryResult result = bucket.query(query);

        LOGGER.debug("Query metrics: {}.", result.info());

        if (result.finalSuccess()) {
            Collection<T> results = result.allRows().stream()
                    .map(mapper)
                    .collect(toList());

            subscriber.onNext(results);

            subscriber.onCompleted();
        } else {
            subscriber.onError(
                    new RuntimeException(
                            String.format("Failed to execute query: %s.", query.n1ql())
                    ));
        }
    }
}
