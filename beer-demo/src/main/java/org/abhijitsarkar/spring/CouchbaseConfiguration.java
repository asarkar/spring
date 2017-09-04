package org.abhijitsarkar.spring;

import com.couchbase.client.java.document.RawJsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.domain.Brewery;
import org.abhijitsarkar.spring.factory.CouchbaseAsyncBucketFactory;
import org.abhijitsarkar.spring.factory.CouchbaseAsyncClusterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@EnableConfigurationProperties(CouchbaseProperties.class)
public class CouchbaseConfiguration {
    @Autowired
    private CouchbaseProperties couchbaseProperties;

    @Bean(destroyMethod = "disconnect")
    CouchbaseAsyncClusterFactory couchbaseAsyncClusterFactory() {
        return CouchbaseAsyncClusterFactory.builder()
                .couchbaseProperties(couchbaseProperties)
                .build();
    }

    @Bean(destroyMethod = "close")
    CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory() {
        return new CouchbaseAsyncBucketFactory(couchbaseAsyncClusterFactory(), couchbaseProperties);
    }

//    @Bean
//    @Lazy
//    CouchbaseClusterFactoryBean couchbaseClusterFactoryBean() {
//        return new CouchbaseClusterFactoryBean(couchbaseProperties, couchbaseEnvironmentFactoryBean().getBean());
//    }
//
//    @Bean
//    @Lazy
//    CouchbaseBucketFactoryBean couchbaseBucketFactoryBean() {
//        return new CouchbaseBucketFactoryBean(couchbaseClusterFactoryBean(), couchbaseProperties);
//    }
//
//    @Bean
//    @Lazy
//    CouchbaseEnvironmentFactoryBean couchbaseEnvironmentFactoryBean() {
//        return new CouchbaseEnvironmentFactoryBean(couchbaseProperties);
//    }
//
//    @Bean
//    @Lazy
//    CouchbaseTemplateFactoryBean couchbaseTemplateFactoryBean() {
//        return new CouchbaseTemplateFactoryBean(
//                couchbaseClusterFactoryBean(),
//                couchbaseBucketFactoryBean(),
//                couchbaseProperties
//        );
//    }
//
//    @Bean
//    @Lazy
//    RepositoryOperationsMappingFactoryBean repositoryOperationsMappingFactoryBean() {
//        return new RepositoryOperationsMappingFactoryBean(couchbaseTemplateFactoryBean(), couchbaseProperties);
//    }
//
//    @Bean
//    @Lazy
//    IndexManagerFactoryBean indexManagerFactoryBean() {
//        return new IndexManagerFactoryBean(couchbaseProperties);
//    }
//
//    @Bean
//    @Lazy
//    CouchbaseRepositoryFactory couchbaseRepositoryFactory() {
//        return new CouchbaseRepositoryFactory(
//                repositoryOperationsMappingFactoryBean(),
//                indexManagerFactoryBean(),
//                couchbaseTemplateFactoryBean()
//        );
//    }

    @Bean
    DbInitializer dbInitializer(CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory) {
        return new DbInitializer(couchbaseAsyncBucketFactory);
    }

    @RequiredArgsConstructor
    @Slf4j
    static class DbInitializer {
        private final CouchbaseAsyncBucketFactory couchbaseAsyncBucketFactory;

        @Value("${couchbase.initialize:false}")
        private boolean initialize;

        @EventListener
        void doWhenApplicationIsReady(ApplicationReadyEvent event) {
            if (initialize) {
                log.debug("Initializing database.");

                ObjectMapper objectMapper = new ObjectMapper();

                try (InputStream breweries = new ClassPathResource("/breweries.json").getInputStream()) {
                    List<Brewery> list1 = objectMapper.readValue(breweries, new TypeReference<List<Brewery>>() {
                    });

                    save(toJsonDocuments(list1, Brewery::getName, objectMapper))
                            .flatMap(x -> {
                                try (InputStream beers = new ClassPathResource("/beers.json").getInputStream()) {
                                    List<Beer> list2 = objectMapper.readValue(beers, new TypeReference<List<Beer>>() {
                                    });

                                    return save(toJsonDocuments(list2, Beer::getName, objectMapper));
                                } catch (IOException e) {
                                    log.error("Failed to initialize beers.", e);
                                    return Observable.empty();
                                }
                            })
                            .toCompletable()
                            .await(1, TimeUnit.MINUTES);
                } catch (IOException e) {
                    log.error("Failed to insert breweries.", e);
                }
            }
        }

        private Observable<RawJsonDocument> save(List<RawJsonDocument> list) {
            return couchbaseAsyncBucketFactory.getInstance()
                    .flatMapObservable(bucket -> list
                            .stream()
                            .map(bucket::upsert)
                            .collect(collectingAndThen(toList(), Observable::from)))
                    .flatMap(obs -> obs)
                    .doOnNext(doc -> log.debug("Successfully saved doc with id: {}.", doc.id()));
        }

        private <T> List<RawJsonDocument> toJsonDocuments(
                Collection<T> list,
                Function<T, String> idMapper,
                ObjectMapper objectMapper
        ) {
            return list.stream()
                    .map(element -> {
                        try {
                            return RawJsonDocument.create(
                                    idMapper.apply(element),
                                    objectMapper.writeValueAsString(element)
                            );
                        } catch (JsonProcessingException e) {
                            log.error("Failed to convert element: {} to JsonStringDocument.", element, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());
        }
    }
}