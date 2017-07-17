package org.abhijitsarkar.service

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.GradleTask
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Abhijit Sarkar
 */
interface GradleAgent {
    fun isGradleProject(dir: File): Boolean
    fun generateLicense(dir: File, options: String): Mono<Pair<String, String>>
}

class GradleAgentImpl(vararg val tasks: String = arrayOf("clean", "downloadLicenses")) : GradleAgent {
    private val log = LoggerFactory.getLogger(GradleAgent::class.java)

    override fun isGradleProject(dir: File): Boolean = File(dir, "build.gradle")
            .run { exists() }
            .let {
                if (!it && !File(dir, "build.gradle.kts").exists()) {
                    log.info("{}: is not a Gradle project.", dir.name)

                    false
                } else true
            }

    override fun generateLicense(dir: File, options: String): Mono<Pair<String, String>> = connection(dir)
            .map { connection ->
                connection.getModel(GradleProject::class.java)
                        .to(connection)
            }
            .filter {
                it.first.tasks.all
                        .map(GradleTask::getName)
                        .intersect(tasks.asIterable())
                        .isNotEmpty()
            }
            .map {
                val projectName = it.first.name
                log.info("Starting Gradle build for the project: {}.", projectName)

                with(it.second.newBuild()) {
                    forTasks(*tasks)
                    if (options.isNotEmpty()) {
                        log.debug("Build will run with the following options: {}.", options)

                        withArguments(options
                                .split("\\s".toRegex())
                                .filter(String::isNotEmpty)
                        )
                    }

                    run()
                }

                log.info("Build is complete for the project: {}.", projectName)

                projectName.to(dir.absolutePath)
            }
            .retry(2)
            .onErrorResume { t -> log.error("Build failed!", t); Mono.empty() }

    private fun connection(dir: File): Mono<ProjectConnection> = gradleDistroUrl(dir)
            .map { url ->
                log.info("Using Gradle distribution: {} defined by the project: {}.",
                        url.takeLastWhile { it != '/' }, dir.name)

                GradleConnector.newConnector()
                        .useDistribution(URI.create(url))
                        .forProjectDirectory(dir)
                        .connect()
            }
            .switchIfEmpty(Mono.just(GradleConnector.newConnector()
                    .forProjectDirectory(dir)
                    .connect()))

    fun gradleDistroUrl(dir: File): Mono<String> = Mono.just(dir)
            .map { Paths.get(it.path, "gradle", "wrapper", "gradle-wrapper.properties") }
            .filter { Files.exists(it) }
            .flatMapMany { Flux.fromStream(Files.lines(it)) }
            .filter { it.contains("distributionUrl") && it.contains("=") }
            .map { it.split("=".toRegex(), 2)[1] }
            .map { it.replace("\\\\".toRegex(), "") }
            .singleOrEmpty()
}