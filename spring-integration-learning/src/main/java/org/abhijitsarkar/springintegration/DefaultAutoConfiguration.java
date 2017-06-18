package org.abhijitsarkar.springintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.Resource;

import static org.springframework.integration.context.IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableConfigurationProperties(RetryProperties.class)
public class DefaultAutoConfiguration {
    @Resource(name = ERROR_CHANNEL_BEAN_NAME)
    private MessageChannel errorChannel;

    @Autowired
    private RetryProperties retryProperties;

    @ConditionalOnMissingBean
    @Bean(PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller(@Value("${inbound.pollingDelayMillis:1000}") long inboundPollingDelayMillis) {
        return Pollers.fixedDelay(inboundPollingDelayMillis).errorChannel(ERROR_CHANNEL_BEAN_NAME);
    }

    @ConditionalOnMissingBean
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        RetryProperties.ExponentialBackOff exponential = retryProperties.getExponentialBackOff();

        backOffPolicy.setInitialInterval(exponential.getInitialIntervalMillis());
        backOffPolicy.setMultiplier(exponential.getMultiplier());
        backOffPolicy.setMaxInterval(exponential.getMaxIntervalMillis());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorMessageSendingRecoverer errorHandler() {
        return new ErrorMessageSendingRecoverer(errorChannel);
    }

    @ConditionalOnMissingBean
    @Bean
    public RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice retryAdvice = new RequestHandlerRetryAdvice();
        retryAdvice.setRetryTemplate(retryTemplate());
        retryAdvice.setRecoveryCallback(errorHandler());

        return retryAdvice;
    }
}
