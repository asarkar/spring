package org.abhijitsarkar.spring.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;

public abstract class BootAbstractState {
    private static final String APP_STARTED_PATTERN =
            "(?:.+): (?:Started (?:.+) in (?:\\d+\\.\\d+) seconds \\(JVM running for (?:\\d+\\.\\d+)\\))";
    private Process started;

    private boolean isStarted() {
        return Objects.nonNull(started) && started.isAlive();
    }

    protected void start() {
        if (isStarted()) {
            throw new IllegalStateException("Already started");
        } else {
            ProcessBuilder pb = new ProcessBuilder(getCommand());
            customize(pb);
            try {
                started = pb
                        .redirectErrorStream(true)
                        .start();

                waitUntilFullyStarted(started);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    protected void customize(ProcessBuilder pb) {
    }

    private void waitUntilFullyStarted(Process started) throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(started.getInputStream()));
            String line;
            while ((line = br.readLine()) != null && !line.matches(APP_STARTED_PATTERN)) {
                sb.append(line).append(lineSeparator);
            }
            if (line != null) {
                sb.append(line).append(lineSeparator);
            }
        } finally {
            System.out.println(sb.toString());
        }
    }

    protected void stop() {
        if (isStarted()) {
            try {
                started.destroyForcibly().waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                started = null;
            }
        } else {
            throw new IllegalStateException("Already stopped");
        }
    }

    protected abstract String[] getCommand();
}
