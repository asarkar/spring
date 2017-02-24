package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class TemplateResolverApp implements CommandLineRunner {
    private final TemplateResolverSvc templateResolverSvc;

    public static void main(String[] args) {
        SpringApplication.run(TemplateResolverApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        templateResolverSvc.resolve();
    }
}
