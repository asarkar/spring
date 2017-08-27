package org.abhijitsarkar.spring;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import org.abhijitsarkar.spring.repository.couchbase.CouchbaseBeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(CouchbaseProperties.class)
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
    @Autowired
    private CouchbaseProperties couchbaseProperties;

    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList("localhost");
    }

    @Override
    protected String getBucketName() {
        return couchbaseProperties.getBucketName();
    }

    @Override
    protected String getBucketPassword() {
        return couchbaseProperties.getBucketPassword();
    }

    @Override
    public Bucket couchbaseClient() throws Exception {
        ClusterManager clusterManager = couchbaseCluster()
                .clusterManager(couchbaseProperties.getAdminUsername(), couchbaseProperties.getAdminPassword());
        String bucketName = getBucketName();
        String bucketPassword = getBucketPassword();

        if (!clusterManager.hasBucket(bucketName)) {
            clusterManager.insertBucket(
                    DefaultBucketSettings.builder()
                            .name(bucketName)
                            .password(bucketPassword)
                            .enableFlush(true)
                            .quota(100) // MB
                            .build()
            );

//            Index.createIndex(bucketName + "_name").on(bucketName, x("name"));
        }

        return super.couchbaseClient();
    }

    @Override
    public ClusterInfo couchbaseClusterInfo() throws Exception {
        return couchbaseCluster()
                .clusterManager(couchbaseProperties.getAdminUsername(), couchbaseProperties.getAdminPassword())
                .info();
    }
}