package org.abhijitsarkar.ufo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.abhijitsarkar.ufo.service.Producer;
import org.springframework.kafka.event.ListenerContainerIdleEvent;

import java.util.Map;

/**
 * @author Abhijit Sarkar
 */
//  Due to type erasure we need to publish an event that resolves the generics parameter we want to filter on
public interface CompletionEvent<T> {

    public static class ProducerCompletedEvent implements CompletionEvent<Producer> {
    }

    @RequiredArgsConstructor
    @Getter
    public static class ConsumerCompletedEvent implements CompletionEvent<ConsumerCompletedEvent> {
        private final ListenerContainerIdleEvent listenerIdleEvent;
        private final Map<String, Map<String, Integer>> analytics;
    }
}