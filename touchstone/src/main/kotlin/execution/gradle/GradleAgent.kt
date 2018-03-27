package org.abhijitsarkar.touchstone.execution.gradle

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Abhijit Sarkar
 */
interface GradleAgent {
    companion object {
        internal fun isGradleProject(path: Path) = Files.isDirectory(path) && Files.exists(path.resolve("build.gradle"))
    }

    fun build(path: Path, gradleTasks: Array<String> = emptyArray())
}

class GradleAgentImpl(private val gradleProperties: GradleProperties) : GradleAgent {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GradleAgent::class.java)
    }

    private fun connection(path: Path): ProjectConnection {
        val gradleDistroUrl: URI? = gradleDistroUrl(path)

        return if (gradleDistroUrl != null) {
            LOGGER.debug("Using Gradle distribution: {} defined by the project: {}.",
                    gradleDistroUrl, path.fileName)

            GradleConnector.newConnector()
                    .useDistribution(gradleDistroUrl)
                    .forProjectDirectory(path.toFile())
        } else {
            GradleConnector.newConnector()
                    .forProjectDirectory(path.toFile())
        }
                .connect()
    }

    internal fun gradleDistroUrl(path: Path) = path
            .resolve("gradle")
            .resolve("wrapper")
            .resolve("gradle-wrapper.properties")
            .let {
                if (Files.exists(it)) {
                    Files.readAllLines(it, UTF_8)
                            .filter { it.contains("distributionUrl") && it.contains("=") }
                            .map { it.split("=".toRegex(), 2)[1] }
                            .map { it.replace("\\\\".toRegex(), "") }
                            .firstOrNull()
                            ?.let(URI::create)
                } else {
                    null
                }
            }

    override fun build(path: Path, gradleTasks: Array<String>) {
        val tasks = if (gradleTasks.isEmpty()) gradleProperties.tasks else gradleTasks
        LOGGER.info("Running Gradle tasks: {} on project: {}", tasks, path.fileName)

        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()

        connection(path)
                .run {
                    try {
                        with(newBuild()) {
                            forTasks(*tasks)
                            if (gradleProperties.options.isNotEmpty()) {
                                withArguments(*gradleProperties.options)
                            }
                            setStandardOutput(out)
                            setStandardError(err)

                            run()
                        }
                    } finally {
                        if (out.size() > 0) {
                            LOGGER.debug("Build output:\n{}", out.toString(UTF_8.name()))
                        }

                        if (err.size() > 0) {
                            LOGGER.warn("Build has the following warnings/errors:\n{}", err.toString(UTF_8.name()))
                        }

                        out.close()
                        err.close()

                        close()
                    }
                }
    }
}