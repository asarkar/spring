package org.abhijitsarkar.ufo.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("sighting.consumer")
@Component
public class ConsumerProperties {
    @Setter
    private Long delayMillis;
    @Setter
    private Long idleEventIntervalMillis;
    @Setter
    private Integer concurrency;
    @Setter
    private String group;
    @Setter
    @Getter
    private boolean autoCommit;
    @Setter
    private Integer numIdleEventsUntilShutdown;

    public long getDelayMillis() {
        return delayMillis == null ? 1000 * 60 : delayMillis;
    }

    public long getIdleEventIntervalMillis() {
        return idleEventIntervalMillis == null ? getDelayMillis() * 3 : idleEventIntervalMillis;
    }

    public int getConcurrency() {
        return concurrency == null ? 3 : concurrency;
    }

    public String getGroup() {
        return group == null ? "ufo" : group;
    }

    public int getNumIdleEventsUntilShutdown() {
        return numIdleEventsUntilShutdown == null ? 1 : numIdleEventsUntilShutdown;
    }
}
