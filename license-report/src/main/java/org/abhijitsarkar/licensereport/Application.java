package org.abhijitsarkar.licensereport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(GitLabProperties.class)
@RequiredArgsConstructor
@Slf4j
public class Application implements CommandLineRunner {
    private final GitLabProperties gitLabProperties;
    private final GitLabClient client;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Tuple2<File, String>> licenseAndProjects = Flux.fromIterable(gitLabProperties.getGroups())
                .flatMap(client::getProjects)
                .filter(t -> {
                    String projectName = t.getT1().getName();

                    return isNotExcluded(projectName, t.getT2()) && isIncluded(projectName, t.getT2());
                })
                .map(Tuple2::getT1)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::clone)
                .filter(this::isGradleProject)
                .map(this::generateLicense)
                .sequential()
                .collectList()
                .block();
    }

    private final boolean isNotExcluded(String projectName, String groupName) {
        return gitLabProperties.getGroups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .filter(g -> {
                    List<String> projects = g.getExcludesProjects();

                    return !projects.contains(projectName);
                })
                .findFirst()
                .isPresent();
    }

    private final boolean isIncluded(String projectName, String groupName) {
        return gitLabProperties.getGroups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .filter(g -> {
                    List<String> projects = g.getIncludesProjects();

                    return CollectionUtils.isEmpty(projects) || projects.contains(projectName);
                })
                .findFirst()
                .isPresent();
    }

    private final Flux<File> clone(Project project) {
        Git git = null;
        try {
            File tmpDir = Files.createTempDirectory(project.getName()).toFile();
            log.info("Cloning repo: {} to: {}.", project.getSshUrl(), tmpDir.getAbsolutePath());

            git = Git.cloneRepository()
                    .setURI(project.getSshUrl())
                    .setDirectory(tmpDir)
                    .call();

            return Flux.just(tmpDir);
        } catch (Exception e) {
            log.error("Failed to clone repo: {}.", project.getSshUrl(), e);

            return Flux.empty();
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    private final boolean isGradleProject(File dir) {
        File buildFile = new File(dir, "build.gradle");
        boolean gradleProject = buildFile.exists();

        if (!gradleProject) {
            log.info("Not a Gradle project. Skipping: {}.", dir.getName());
        }

        return gradleProject;
    }

    private final Tuple2<File, String> generateLicense(File dir) {
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(dir)
                .connect();

        GradleProject project = connection.getModel(GradleProject.class);

        log.info("Launching Gradle build for project: {}.", project.getName());

        try {
            BuildLauncher build = connection.newBuild();
            build.forTasks("clean", "downloadLicenses");

//                        build.withArguments("--no-search-upward", "-i", "--project-dir", "someProjectDir");

            build.run();

            log.info("Build is done for project: {}.", project.getName());

            return Tuples.of(dir, project.getName());
        } finally {
            connection.close();
        }
    }
}
