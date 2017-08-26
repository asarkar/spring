package org.abhijitsarkar.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BeerDemoApplication {
    static final String COUCHBASE_PROFILE = "couchbase";
    static final String JPA_PROFILE = "jpa";

    public static void main(String[] args) {
        new SpringApplicationBuilder(BeerDemoApplication.class)
                .web(true)
                .run(args);
    }
}
