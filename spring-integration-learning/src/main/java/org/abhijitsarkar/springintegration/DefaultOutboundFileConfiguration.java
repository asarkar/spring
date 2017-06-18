package org.abhijitsarkar.springintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.MessageHandler;

import java.io.File;

import static java.util.Collections.singletonList;
import static org.abhijitsarkar.springintegration.Application.OUTPUT_GATEWAY;
import static org.springframework.integration.file.support.FileExistsMode.REPLACE;

/**
 * @author Abhijit Sarkar
 */
@MessageEndpoint
@Profile("outbound-default")
public class DefaultOutboundFileConfiguration {
    @Value("${outbound.file.dir:build}")
    private String out;

    @Autowired
    private RequestHandlerRetryAdvice retryAdvice;

    @Bean
    @ServiceActivator(inputChannel = OUTPUT_GATEWAY)
    public MessageHandler messageHandler() {
        FileWritingMessageHandler messageHandler = Files.outboundAdapter(new File(out))
                .get();

        messageHandler.setAutoCreateDirectory(false);
        messageHandler.setDeleteSourceFiles(false);
        messageHandler.setExpectReply(false);
        messageHandler.setFileExistsMode(REPLACE);
        messageHandler.setAdviceChain(singletonList(retryAdvice));
        messageHandler.setFileNameGenerator(new FileNameGenerator());

        return messageHandler;
    }
}
