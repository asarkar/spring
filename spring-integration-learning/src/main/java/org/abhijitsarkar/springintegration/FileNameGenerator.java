package org.abhijitsarkar.springintegration;

import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Abhijit Sarkar
 */
public class FileNameGenerator implements org.springframework.integration.file.FileNameGenerator {
    @Override
    public String generateFileName(Message<?> message) {
        MessageHeaders headers = message.getHeaders();

        Object filename = headers.get(FileHeaders.FILENAME);

        if (filename == null) {
            filename = headers.get(FileHeaders.REMOTE_FILE);
        }

        if (filename == null) {
            filename = DateTimeFormatter.ofPattern("yyyyMMdd-kkmm").format(LocalDateTime.now()) + ".out";
        }

        return filename.toString();
    }
}
