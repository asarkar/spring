package org.abhijitsarkar.springintegration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Publisher;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.abhijitsarkar.springintegration.Application.INPUT_CHANNEL;

/**
 * @author Abhijit Sarkar
 */
@MessageEndpoint
@Profile("inbound-default")
public class DefaultInboundStreamConfiguration {
    @Bean
    @Publisher(channel = INPUT_CHANNEL)
    // no-arg method needs PollableChannel
    // http://docs.spring.io/spring-integration/reference/html/messaging-endpoints-chapter.html#gateway-calling-no-argument-methods
    public Message<InputStream> source() {
        InputStream is = new ByteArrayInputStream(String.format("Ran in the default mode.").getBytes(UTF_8));

        Message<InputStream> message = MessageBuilder
                .withPayload(is)
                .setHeader(FileHeaders.FILENAME, "test.out")
                .build();

        return message;
    }

    @Bean(INPUT_CHANNEL)
    public PollableChannel inputChannel() {
        return new QueueChannel(1);
    }
}
