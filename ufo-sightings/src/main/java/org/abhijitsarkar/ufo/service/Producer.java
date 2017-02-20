package org.abhijitsarkar.ufo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.ufo.domain.CompletionEvent.ProducerCompletedEvent;
import org.abhijitsarkar.ufo.domain.ProducerProperties;
import org.abhijitsarkar.ufo.domain.Sighting;
import org.abhijitsarkar.ufo.repository.Crawler;
import org.abhijitsarkar.ufo.repository.ListenableToCompletableFutureAdapter;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.function.Supplier;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class Producer implements CommandLineRunner {
    private static final int BATCH_SIZE = 12;
    private static final int CONCURRENCY = Runtime.getRuntime().availableProcessors() * 2;

    private final KafkaOperations<String, Sighting> kafkaTemplate;
    private final ProducerProperties producerProperties;
    private final Crawler crawler;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    Flux<Sighting> getSightings() {
        YearMonth from = producerProperties.getFromYearMonth();
        YearMonth to = producerProperties.getToYearMonth();

        int months = (int) from.until(to, MONTHS) + 1;
        int numBatches = (int) Math.ceil((double) months / BATCH_SIZE);

        return Flux.range(1, numBatches - 1)
                .zipWith(Flux.intervalMillis(producerProperties.getDelayMillis()), (i, delay) -> i)
                .scanWith(new TupleSupplier(true, from, to),
                        (t, i) -> new TupleSupplier(false, t.getT1(), to).get())
                .groupBy(t -> t.getT1().getYear())
                .flatMap(g -> g
                        .publishOn(Schedulers.parallel())
                        .flatMap(t -> crawler.getSightings(t.getT1(), t.getT2())), CONCURRENCY);
    }

    @RequiredArgsConstructor
    private static final class TupleSupplier implements Supplier<Tuple2<YearMonth, YearMonth>> {
        private final boolean first;
        private final YearMonth from;
        private final YearMonth to;

        @Override
        public Tuple2<YearMonth, YearMonth> get() {
            YearMonth adjustedFrom = from.with(new NextYearOrSame(first));
            YearMonth adjustedTo = adjustedFrom.with(new EndOfYearOrSame(to));

            return Tuples.of(adjustedFrom, adjustedTo);
        }
    }

    @RequiredArgsConstructor
    private static final class EndOfYearOrSame implements TemporalAdjuster {
        private final YearMonth to;

        @Override
        public Temporal adjustInto(Temporal temporal) {
            YearMonth from = YearMonth.from(temporal);

            YearMonth maybeTo = from.withMonth(DECEMBER.getValue());

            return maybeTo.isBefore(to) ? maybeTo : to;
        }
    }

    @RequiredArgsConstructor
    private static final class NextYearOrSame implements TemporalAdjuster {
        private final boolean first;

        @Override
        public Temporal adjustInto(Temporal temporal) {
            YearMonth from = YearMonth.from(temporal);

            return first ? from : from.withMonth(JANUARY.getValue()).plusYears(1);
        }
    }

    @Override
    public void run(String... args) {
        getSightings()
                .map(kafkaTemplate::sendDefault)
                .map(ListenableToCompletableFutureAdapter::new)
                .flatMap(Mono::fromFuture)
                .doOnComplete(() -> {
                    if (eventPublisher != null) {
                        eventPublisher.publishEvent(new ProducerCompletedEvent());
                    }
                })
                .subscribe(s -> {
                    RecordMetadata metadata = s.getRecordMetadata();
                    log.debug("Wrote message to topic: {}, partition: {}.", metadata.topic(), metadata.partition());
                }, t -> log.error(t.getMessage(), t));
    }
}
