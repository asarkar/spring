package org.abhijitsarkar.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.aws.s3.S3Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
@RequiredArgsConstructor
public class FilenameHeaderMessageProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Map<String, Object> inHeaders = in.getHeaders();
        log.debug("In headers: {}.", inHeaders);

        Message out = exchange.getOut();

        // Without this, out body is null
        out.setBody(in.getBody());

        Object filename = inHeaders.get(Exchange.FILE_NAME);

        if (Objects.isNull(filename)) {
            filename = inHeaders.get(S3Constants.KEY);

            if (Objects.isNull(filename)) {
                filename = defaultFilename();
            }
        }
        out.setHeader(Exchange.FILE_NAME, filename);
    }

    private final String defaultFilename() {
        return DateTimeFormatter.ofPattern("yyyyMMdd-kkmm").format(LocalDateTime.now()) + ".out";
    }
}
