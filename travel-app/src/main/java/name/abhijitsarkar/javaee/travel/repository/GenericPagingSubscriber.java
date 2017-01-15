package name.abhijitsarkar.javaee.travel.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlMetrics;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import lombok.Builder;
import name.abhijitsarkar.javaee.travel.domain.Page;
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
public class GenericPagingSubscriber<T> implements Observable.OnSubscribe<Page<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericPagingSubscriber.class);
    private final N1qlQuery query;
    private final Bucket bucket;
    private final Function<N1qlQueryRow, T> mapper;
    private final int pageSize;
    private final int pageNum;

    @Override
    public void call(Subscriber<? super Page<T>> subscriber) {
        LOGGER.debug("Executing query: {}.", query.n1ql());

        N1qlQueryResult result = bucket.query(query);

        N1qlMetrics metrics = result.info();

        LOGGER.debug("Query metrics: {}.", metrics);

        if (result.finalSuccess()) {
            Collection<T> results = result.allRows().stream()
                    .map(mapper)
                    .collect(toList());

            int totalResultCount = metrics.resultCount();
            int numPages = this.pageSize > 0 ?
                    (int) Math.ceil((totalResultCount * 1.0d) / this.pageSize) : 1;
            int pageSize = this.pageSize >= 0 ? this.pageSize : totalResultCount;
            int pageNum = this.pageNum > 0 ? this.pageNum : 1;

            Page<T> page = new Page<>(pageNum, pageSize, numPages, results);

            subscriber.onNext(page);

            subscriber.onCompleted();
        } else {
            subscriber.onError(
                    new RuntimeException(
                            String.format("Failed to execute query: %s.", query.n1ql())
                    ));
        }
    }
}
