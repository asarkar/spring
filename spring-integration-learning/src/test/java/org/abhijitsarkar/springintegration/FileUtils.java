package org.abhijitsarkar.springintegration;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
@Slf4j
public class FileUtils {
    private FileUtils() {
    }

    public static final String content(Path p) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(p.toFile(), UTF_8.name());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static final List<Path> waitForFileCreation(Path dir, Duration timeout) {
        List<Path> paths = null;

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            paths = Files.list(dir).collect(toList());

            dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

            Duration duration = Duration.from(timeout);
            WatchKey key = watcher.poll(10, TimeUnit.SECONDS);

            for (; !duration.isNegative();
                 key = watcher.poll(10, TimeUnit.SECONDS), duration = duration.minusSeconds(10)) {
                if (key == null) {
                    continue;
                }

                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = watchEvent.kind();

                    Path p = ((WatchEvent<Path>) watchEvent).context();

                    if (OVERFLOW == kind) {
                        log.warn("Event lost or discarded! {}.", p.toFile().getAbsolutePath());

                        continue;
                    } else if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind) {
                        log.debug("Entry created or modified: {}.", p.toFile().getAbsolutePath());

                        paths.add(p);
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            return unmodifiableList(paths);
        }
    }
}
