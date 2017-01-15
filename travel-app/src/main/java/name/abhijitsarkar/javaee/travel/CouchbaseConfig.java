package name.abhijitsarkar.javaee.travel;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Abhijit Sarkar
 */
@Configuration
@Profile("!noDB")
public class CouchbaseConfig {
    @Value("${COUCHBASE_NODES}")
    private String nodes;

    @Value("${COUCHBASE_BUCKET_NAME}")
    private String bucketName;

    @Value("${COUCHBASE_BUCKET_PASSWORD}")
    private String bucketPassword;

    @Bean
    Cluster cluster() {
        return CouchbaseCluster.create(nodes.split("\\s+"));
    }

    @Bean
    Bucket bucket() {
        return cluster().openBucket(bucketName, bucketPassword);
    }
}
