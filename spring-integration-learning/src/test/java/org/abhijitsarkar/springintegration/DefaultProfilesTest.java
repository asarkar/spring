package org.abhijitsarkar.springintegration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.abhijitsarkar.springintegration.FileUtils.waitForFileCreation;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultProfilesTest {
    @Value("${outbound.file.dir:build}")
    private String out;

    @Test
    public void fileWritten() {
        List<Path> paths = waitForFileCreation(Paths.get(out), Duration.ofSeconds(2l));

        String content = paths.stream()
                .filter(p -> p.toFile().getName().equalsIgnoreCase("test.out"))
                .findFirst()
                .map(FileUtils::content)
                .orElse("");

        assertThat(content).isEqualTo("Ran in the default mode.");
    }
}
