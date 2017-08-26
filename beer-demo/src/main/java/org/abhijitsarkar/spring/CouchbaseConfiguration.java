package org.abhijitsarkar.spring;

import org.abhijitsarkar.spring.repository.couchbase.CouchbaseBeerRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import java.util.Arrays;
import java.util.List;

import static org.abhijitsarkar.spring.BeerDemoApplication.COUCHBASE_PROFILE;

/**
 * @author Abhijit Sarkar
 */
@Profile(COUCHBASE_PROFILE)
@Configuration
@EnableCouchbaseRepositories(basePackageClasses = CouchbaseBeerRepository.class)
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList("localhost");
    }

    @Override
    protected String getBucketName() {
        return "beer-sample";
    }

    @Override
    protected String getBucketPassword() {
        return "";
    }
}