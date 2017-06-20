package org.abhijitsarkar.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static org.apache.camel.Exchange.FILE_NAME;
import static org.apache.camel.component.aws.s3.S3Constants.KEY;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
public class FilenameHeaderMessageProcessor implements org.apache.camel.Processor {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-kkmm");

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Map<String, Object> inHeaders = in.getHeaders();
        log.debug("In headers: {}.", inHeaders);

        Message out = exchange.getOut();

        // Without this, out body is null
        out.setBody(in.getBody());

        Object filename = inHeaders.computeIfAbsent(FILE_NAME,
                k -> Optional.ofNullable(inHeaders.get(KEY))
                        .orElse(defaultFilename())
        );

        out.setHeader(FILE_NAME, filename);
    }

    private final String defaultFilename() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ".out";
    }
}
