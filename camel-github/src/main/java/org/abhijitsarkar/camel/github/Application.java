package org.abhijitsarkar.camel.github;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {
    public static final String ENDPOINT = "GitHubEndpoint";
    public static final String USERNAME = "GitHubUsername";
    public static final String REPO = "GitHubRepo";

    @Autowired
    private ProducerTemplate producerTemplate;

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Application.class)
                .web(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            producerTemplate.sendBodyAndHeader("direct:start", args[0], ENDPOINT, "direct:end");
        } else {
            log.info("Nothing to do; did you forget to pass username?");
        }
    }
}
