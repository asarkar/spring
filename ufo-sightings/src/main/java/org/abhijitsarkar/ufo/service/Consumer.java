package org.abhijitsarkar.ufo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.ufo.domain.CompletionEvent.ConsumerCompletedEvent;
import org.abhijitsarkar.ufo.domain.Sighting;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.event.ListenerContainerIdleEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableMap;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class Consumer {
    private final Map<String, Map<String, Integer>> analytics = new ConcurrentHashMap<>();

    public Map<String, Map<String, Integer>> getAnalytics() {
        return unmodifiableMap(analytics);
    }

    @KafkaListener(id = "ufo", topics = "ufo", containerFactory = "ufoContainerFactory")
    public void listen(List<Sighting> sightings) {
        sightings.stream()
                .map(s -> {
                    String state = toUpperCaseOrUnknown(s.getState());
                    String shape = toUpperCaseOrUnknown(s.getShape());
                    LocalDateTime eventDateTime = s.getEventDateTime();
                    String month = unknownEventDateTime(eventDateTime) ? "UNKNOWN"
                            : eventDateTime.getMonth().name();
                    String year = unknownEventDateTime(eventDateTime) ? "UNKNOWN"
                            : Integer.toString(eventDateTime.getYear());

                    return Tuples.of(state, shape, month, year);
                })
                .forEach(t -> {
                    analytics.merge("state", singletonMap(t.getT1()), this::merge);
                    analytics.merge("shape", singletonMap(t.getT2()), this::merge);
                    analytics.merge("month", singletonMap(t.getT3()), this::merge);
                    analytics.merge("year", singletonMap(t.getT4()), this::merge);
                });
    }

    private boolean unknownEventDateTime(LocalDateTime eventDateTime) {
        return eventDateTime.getYear() == 1;
    }

    private Map<String, Integer> merge(Map<String, Integer> oldMap, Map<String, Integer> newMap) {
        newMap.forEach((k, v) -> oldMap.merge(k, v, (i, j) -> i + j));

        return oldMap;
    }

    private Map<String, Integer> singletonMap(String key) {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put(key, 1);

        return map;
    }

    private String toUpperCaseOrUnknown(String s) {
        return StringUtils.isEmpty(s) ? "UNKNOWN" : s.toUpperCase();
    }

    // Event listeners will see events for all containers; so, in the example above, we narrow the events received
    // based on the listener id. Since containers created for the @KafkaListener support concurrency,
    // the actual containers are named id-n where the n is a unique value for each instance to support the concurrency.
    // Hence we use startsWith in the condition.
    @EventListener(condition = "event.listenerId.startsWith('ufo-')")
    public ConsumerCompletedEvent eventHandler(ListenerContainerIdleEvent event) {
        return new ConsumerCompletedEvent(event, getAnalytics());
    }
}
