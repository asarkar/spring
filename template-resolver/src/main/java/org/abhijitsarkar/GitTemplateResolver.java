package org.abhijitsarkar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

import static java.nio.file.Files.deleteIfExists;

/**
 * @author Abhijit Sarkar
 */
@RequiredArgsConstructor
@Slf4j
public class GitTemplateResolver extends FileTemplateResolver {
    @NonNull
    private final String baseUri;
    @NonNull
    private final String localRepoPath;

    @PostConstruct
    void postConstruct() throws IOException, GitAPIException {
        File file = new File(localRepoPath);
        if (!deleteIfExists(file.toPath())) {
            throw new IOException("Could not delete local repo: " + file.getAbsolutePath());
        }

        log.debug("Cloning from: {} to: {}.", baseUri, localRepoPath);

        try (Git result = Git.cloneRepository()
                .setURI(baseUri)
                .setDirectory(file)
                .call()) {
            // nothing to do, either clone worked or blew up
        }
    }
}
