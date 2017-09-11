package org.abhijitsarkar.spring.beer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {CouchbaseRepositoriesAutoConfiguration.class})
@EnableAsync
public class BeerDemoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BeerDemoApplication.class)
                .web(true)
                .run(args);
    }
}
