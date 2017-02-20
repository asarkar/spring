package org.abhijitsarkar.ufo;

import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.ufo.domain.CompletionEvent.ConsumerCompletedEvent;
import org.abhijitsarkar.ufo.domain.CompletionEvent.ProducerCompletedEvent;
import org.abhijitsarkar.ufo.domain.ConsumerProperties;
import org.abhijitsarkar.ufo.service.PrettyPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Abhijit Sarkar
 */
@Component
@Slf4j
public class TheTerminator {
    @Autowired
    private ConsumerProperties consumerProperties;
    @Autowired
    private ApplicationContext appCtx;

    private AtomicInteger counter;

    @PostConstruct
    void postConstruct() {
        int max = consumerProperties.getNumIdleEventsUntilShutdown() * consumerProperties.getConcurrency()
                + 1; // for the producer

        counter = new AtomicInteger(max);
    }

    @EventListener(ProducerCompletedEvent.class)
    public void listenToProducerCompletedEvent() {
        log.info("Producer completed.");
        counter.decrementAndGet();
    }

    @EventListener(ConsumerCompletedEvent.class)
    public void listenToConsumerCompletedEvent(ConsumerCompletedEvent event) throws InterruptedException {
        log.info("Detected idle consumer.");

        if (counter.decrementAndGet() == 0) {
            Map<String, Map<String, Integer>> analytics = event.getAnalytics();
            PrettyPrinter.print(analytics.get("state"), "STATE", "COUNT");
            PrettyPrinter.print(analytics.get("shape"), "SHAPE", "COUNT");
            PrettyPrinter.print(analytics.get("month"), "MONTH", "COUNT");
            PrettyPrinter.print(analytics.get("year"), "YEAR", "COUNT");

            log.warn("Application will now shutdown!");

            KafkaMessageListenerContainer listenerContainer =
                    (KafkaMessageListenerContainer) event.getListenerIdleEvent().getSource();
            listenerContainer.stop();
            SpringApplication.exit(appCtx);
        }
    }
}
