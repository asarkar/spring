package org.abhijitsarkar.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.abhijitsarkar.spring.domain.Beer;
import org.abhijitsarkar.spring.domain.Brewery;
import org.abhijitsarkar.spring.repository.jpa.JpaBeerRepository;
import org.abhijitsarkar.spring.repository.jpa.JpaBreweryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.abhijitsarkar.spring.BeerDemoApplication.JPA_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Profile(JPA_PROFILE)
@Configuration
@EnableJpaRepositories(basePackageClasses = JpaBeerRepository.class)
//@EnableScheduling
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class JpaConfiguration { // implements SchedulingConfigurer {
    @Bean
    @ConfigurationProperties("datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .build();
    }

//    @Bean(destroyMethod = "shutdown")
//    public Executor taskScheduler() {
//        return Executors.newScheduledThreadPool(2);
//    }

    @Bean
    DbInitializer dbInitializer() {
        return new DbInitializer();
    }

//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setScheduler(taskScheduler());
//        taskRegistrar.addTriggerTask(this::init, ctx -> ctx.lastActualExecutionTime() == null ?
//                Date.from(Instant.now().plus(5, SECONDS)) : null);
//    }

    static class DbInitializer {
        @Autowired
        @Lazy
        private JpaBeerRepository jpaBeerRepository;
        @Autowired
        @Lazy
        private JpaBreweryRepository jpaBreweryRepository;

        @EventListener
        void doWhenApplicationIsReady(ApplicationReadyEvent event) throws IOException {
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
}
