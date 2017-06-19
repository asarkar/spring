package org.abhijitsarkar.camel.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.apache.camel.component.aws.s3.S3Constants.KEY;
import static org.apache.camel.component.aws.s3.S3Constants.LAST_MODIFIED;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
public class S3LastModifiedFilter {
    private static final DateTimeFormatter FORMATTER = ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

    private final long lastModifiedWithinSeconds;
    private final String prefix;

    public boolean accept(@Header(KEY) String key, @Header(LAST_MODIFIED) Date lastModified) {
        if (key.equals(prefix)) {
            return false;
        }

        Instant lastModifiedInstant = lastModified.toInstant();
        Instant now = Instant.now();

        boolean accepted = Duration.between(now, lastModifiedInstant).abs().getSeconds() <= lastModifiedWithinSeconds;

        log.debug("File: {} was last modified on: {}. It is {}accepted for processing.", key,
                FORMATTER.format(lastModifiedInstant), accepted ? "" : "not ");

        return accepted;
    }
}
