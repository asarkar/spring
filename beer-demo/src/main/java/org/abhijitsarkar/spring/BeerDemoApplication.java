package org.abhijitsarkar.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.domain.Brewery;
import org.abhijitsarkar.spring.repository.jpa.JpaBeerRepository;
import org.abhijitsarkar.spring.repository.jpa.JpaBreweryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BeerDemoApplication {
    static final String COUCHBASE_PROFILE = "couchbase";
    static final String JPA_PROFILE = "jpa";

    @Autowired
    @Lazy
    private JpaBeerRepository jpaBeerRepository;
    @Autowired
    @Lazy
    private JpaBreweryRepository jpaBreweryRepository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(BeerDemoApplication.class)
                .web(true)
                .run(args);
    }

    @EventListener
    void doWhenApplicationIsReady(ApplicationReadyEvent event) throws IOException {
        boolean jpaProfileEnabled = event.getApplicationContext().getEnvironment().acceptsProfiles(JPA_PROFILE);

        if (!jpaProfileEnabled) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream breweries = new ClassPathResource("/breweries.json").getInputStream()) {
            List<Brewery> list = objectMapper.readValue(breweries, new TypeReference<List<Brewery>>() {
            });

            jpaBreweryRepository.save(list);
        }

        try (InputStream beers = new ClassPathResource("/beers.json").getInputStream()) {
            List<Beer> list = objectMapper.readValue(beers, new TypeReference<List<Beer>>() {
            });

            jpaBeerRepository.save(list);
        }
    }
}
