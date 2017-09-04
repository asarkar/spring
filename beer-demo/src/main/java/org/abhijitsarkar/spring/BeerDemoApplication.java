package org.abhijitsarkar.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = {CouchbaseRepositoriesAutoConfiguration.class})
public class BeerDemoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BeerDemoApplication.class)
                .web(true)
                .run(args);
    }
}
