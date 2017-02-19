package org.abhijitsarkar.ufo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@Builder
@Getter
@ToString
@JsonDeserialize(builder = Sighting.SightingBuilder.class)
public class Sighting {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(?<amount>\\d*\\.?\\d+)\\s*(?<unit>[a-zA-Z]+)$");
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime eventDateTime;
    private String shape;
    private String state;
    private String city;
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private String summary;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SightingBuilder {
        @JsonIgnore
        public SightingBuilder eventDateTime(String eventDateTime, YearMonth yearMonth) {
            try {
                this.eventDateTime = LocalDateTime.parse(eventDateTime, newDateTimeFormatter(yearMonth));
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse datetime: {}.", eventDateTime);
                this.eventDateTime = LocalDateTime.of(1, 1, 1, 0, 0);
            }

            return this;
        }

        public SightingBuilder eventDateTime(String eventDateTime) {
            try {
                this.eventDateTime = LocalDateTime.parse(eventDateTime, DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse datetime: {}.", eventDateTime);
                this.eventDateTime = LocalDateTime.of(1, 1, 1, 0, 0);
            }

            return this;
        }

        @JsonIgnore
        private final DateTimeFormatter newDateTimeFormatter(YearMonth yearMonth) {
            return new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
                    .appendLiteral('/')
                    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
                    .appendLiteral('/')
                    .appendValueReduced(ChronoField.YEAR, 2, 4, yearMonth.getYear())
                    .appendPattern("[ HH:mm]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .toFormatter();
        }

        public SightingBuilder duration(String duration) {
            Matcher matcher = DURATION_PATTERN.matcher(duration);
            if (matcher.find() && matcher.groupCount() >= 2) {
                try {
                    String u = matcher.group("unit").toUpperCase();
                    u = u.endsWith("S") ? u : u + "S";
                    TemporalUnit unit = ChronoUnit.valueOf(u);
                    int amount = Integer.valueOf(matcher.group("amount"));

                    this.duration = Duration.of(amount, unit);
                } catch (RuntimeException e) {
                    log.warn("Failed to parse duration: {} for event datetime: {}.", duration, eventDateTime);
                    this.duration = Duration.ofSeconds(-1);
                }
            } else {
                log.warn("Failed to parse duration: {} for event datetime: {}.", duration, eventDateTime);
                this.duration = Duration.ofSeconds(-1);
            }

            return this;
        }
    }
}
