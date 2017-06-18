package org.abhijitsarkar.springintegration.http;

import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.springintegration.FileNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.LoggingHandler.Level;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.abhijitsarkar.springintegration.Application.OUTPUT_GATEWAY;
import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * @author Abhijit Sarkar
 */
@MessageEndpoint
@Profile("outbound-http")
@Slf4j
public class OutboundHttpConfiguration {
    private static final String RESPONSE_HANDLER__CHANNEL = "responseHandlerChannel";
    @Autowired
    private HttpProperties httpProperties;

    @Autowired
    private RequestHandlerRetryAdvice retryAdvice;

    @Bean
    @ServiceActivator(inputChannel = OUTPUT_GATEWAY)
    public MessageHandler messageHandler() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        requestFactory.setConnectTimeout(httpProperties.getConnectTimeout());
        requestFactory.setReadTimeout(httpProperties.getReadTimeout());

        String url = UriComponentsBuilder.fromUriString(httpProperties.getBaseUrl())
                .path(httpProperties.getPath())
                .build()
                .toUriString();

        HttpRequestExecutingMessageHandler outboundGateway = Http.outboundGateway(url)
                .httpMethod(HttpMethod.valueOf(httpProperties.getMethod()))
                .headerMapper(new HttpHeaderMapper(httpProperties))
                .expectedResponseType(HttpEntity.class)
                .requestFactory(requestFactory)
                .uriVariablesFunction(this::uriVariablesMap)
                .get();

        outboundGateway.setOutputChannelName(RESPONSE_HANDLER__CHANNEL);
        outboundGateway.setExpectReply(true);
        outboundGateway.setAdviceChain(singletonList(retryAdvice));

        return outboundGateway;
    }

    private final Map<String, Object> uriVariablesMap(Message<Object> message) {
        String filename = new FileNameGenerator().generateFileName(message);

        Map<String, Object> variables = new HashMap<>();
        variables.put("filename", filename);

        return variables;
    }

    @Bean
    @ServiceActivator(inputChannel = RESPONSE_HANDLER__CHANNEL)
    @SuppressWarnings("unchecked")
    public MessageHandler responseHandler() {
        return new AbstractMessageHandler() {
            @Override
            protected void handleMessageInternal(Message<?> message) throws Exception {
                if (message.getPayload() instanceof HttpEntity) {
                    HttpEntity<String> responseEntity = (HttpEntity<String>) message.getPayload();

                    log.info("Location header: {}.", message.getHeaders().get(LOCATION));
                } else {
                    log.warn("Unexpected response type: {}.", message.getPayload());
                    new LoggingHandler(Level.INFO).handleMessage(message);
                }
            }
        };
    }
}
