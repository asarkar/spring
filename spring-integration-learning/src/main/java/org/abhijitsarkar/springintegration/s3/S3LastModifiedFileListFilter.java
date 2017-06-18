package org.abhijitsarkar.springintegration.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.file.filters.AbstractFileListFilter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class S3LastModifiedFileListFilter extends AbstractFileListFilter<S3ObjectSummary> {
    private static final DateTimeFormatter FORMATTER = ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

    private final long lastModifiedWithinSeconds;

    public S3LastModifiedFileListFilter(long lastModifiedWithinSeconds) {
        this.lastModifiedWithinSeconds = lastModifiedWithinSeconds;
    }

    @Override
    public boolean accept(S3ObjectSummary file) {
        Instant lastModified = file.getLastModified().toInstant();
        Instant now = Instant.now();

        boolean accepted = Duration.between(now, lastModified).abs().getSeconds() <= lastModifiedWithinSeconds;

        log.debug("File: {} was last modified on: {}. It is {}accepted for processing.", file.getKey(),
                FORMATTER.format(lastModified), accepted ? "" : "not ");

        return accepted;
    }
}
