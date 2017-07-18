package org.abhijitsarkar

import org.abhijitsarkar.client.GitLabClient
import org.abhijitsarkar.client.GitLabProperties
import org.abhijitsarkar.domain.License
import org.abhijitsarkar.service.GradleAgent
import org.abhijitsarkar.service.JGitAgent
import org.abhijitsarkar.service.ReportParser
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
@EnableConfigurationProperties(GitLabProperties::class, ApplicationProperties::class)
class Application(
        val gitLabProperties: GitLabProperties,
        val applicationProperties: ApplicationProperties,
        val gitLabClient: GitLabClient,
        val jGitAgent: JGitAgent,
        val gradleAgent: GradleAgent,
        val reportParser: ReportParser,
        val eventPublisher: ApplicationEventPublisher
) : CommandLineRunner {
    override fun run(vararg args: String): Unit {
        // Immutable view
        val groups: Map<String, GitLabProperties.GroupProperties> = gitLabProperties.groups

        fun isNotExcluded(projectName: String, groupName: String) =
                gitLabProperties
                        .groups[groupName]
                        ?.excludedProjects
                        ?.run { !containsKey(projectName) } ?: true

        fun isIncluded(projectName: String, groupName: String) =
                gitLabProperties
                        .groups[groupName]
                        ?.includedProjects
                        ?.run { isEmpty() || containsKey(projectName) } ?: true

        Flux.fromIterable(groups.entries)
                .flatMap {
                    gitLabClient.projects(it.toPair())
                }
                .filter {
                    val projectName = it.second.name
                    val groupName = it.first

                    isNotExcluded(projectName, groupName) && isIncluded(projectName, groupName)
                }
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap { jGitAgent.clone(it.second, gitLabProperties.groups[it.first]) }
                .filter(gradleAgent::isGradleProject)
                .flatMap { gradleAgent.generateLicense(it, applicationProperties.gradle.options) }
                .flatMap(reportParser::parseReport)
                .sequential()
                .sort(compareBy({ it.first }, { it.second.valid }, { it.second.url }))
                .collectMultimap(Pair<String, License>::first, Pair<String, License>::second)
                .map(::LicenseGeneratedEvent)
                .doOnNext { eventPublisher.publishEvent(it) }
                .block(Duration.ofMinutes(applicationProperties.timeoutMinutes))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(Application::class.java)
                    .web(WebApplicationType.NONE)
                    .run(*args)
        }
    }
}
