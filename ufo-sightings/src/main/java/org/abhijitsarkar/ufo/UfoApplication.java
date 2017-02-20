package org.abhijitsarkar.ufo;

import org.abhijitsarkar.ufo.domain.ConsumerProperties;
import org.abhijitsarkar.ufo.domain.KafkaProperties;
import org.abhijitsarkar.ufo.domain.ProducerProperties;
import org.abhijitsarkar.ufo.domain.Sighting;
import org.abhijitsarkar.ufo.domain.SightingDeserializer;
import org.abhijitsarkar.ufo.domain.SightingSerializer;
import org.abhijitsarkar.ufo.repository.CrawlerImpl;
import org.abhijitsarkar.ufo.service.Producer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode.BATCH;

@SpringBootApplication
@EnableKafka
public class UfoApplication {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private ProducerProperties producerProperties;
    @Autowired
    private ConsumerProperties consumerProperties;

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(UfoApplication.class)
                .web(false)
                .run(args);

        shutdownLatch.await();
    }

    @EventListener
    public void listenToContextClosedEvent(ContextClosedEvent event) {
        shutdownLatch.countDown();
    }

    @Bean
    public KafkaOperations<String, Sighting> ufoKafkaTemplate() {
        KafkaTemplate<String, Sighting> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaProperties.getTopic());
        return kafkaTemplate;
    }

    private ProducerFactory<String, Sighting> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SightingSerializer.class.getName());

        return props;
    }

    @Bean
    public Producer producer() {
        return new Producer(ufoKafkaTemplate(), producerProperties, new CrawlerImpl());
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Sighting>> ufoContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Sighting> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(consumerProperties.getConcurrency());
        factory.setBatchListener(true);
        // To set container properties, you must use the getContainerProperties() method on the factory.
        // It is used as a template for the actual properties injected into the container
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setPollTimeout(consumerProperties.getDelayMillis());
        containerProperties.setAckMode(BATCH);
        containerProperties.setIdleEventInterval(consumerProperties.getIdleEventIntervalMillis());
        return factory;
    }

    public ConsumerFactory<String, Sighting> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SightingDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperties.isAutoCommit());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroup());

        return props;
    }
}
