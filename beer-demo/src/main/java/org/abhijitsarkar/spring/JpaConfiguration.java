package org.abhijitsarkar.spring;

import org.abhijitsarkar.spring.repository.jpa.JpaBeerRepository;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

import static org.abhijitsarkar.spring.BeerDemoApplication.JPA_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Profile(JPA_PROFILE)
@Configuration
@EnableJpaRepositories(basePackageClasses = JpaBeerRepository.class)
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class JpaConfiguration {
    @Bean
    @ConfigurationProperties("datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .build();
    }
}
