package org.abhijitsarkar.service

import org.abhijitsarkar.domain.License
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

/**
 * @author Abhijit Sarkar
 */
typealias ProjectName = String
typealias LicenseDir = String

interface ReportParser {
    fun parseReport(pair: Pair<ProjectName, LicenseDir>): ParallelFlux<Pair<ProjectName, License>>
}

internal class ReportParserImpl constructor(val linkVerifier: LinkVerifier) : ReportParser {
    private val log = LoggerFactory.getLogger(ReportParser::class.java)

    override fun parseReport(pair: Pair<ProjectName, LicenseDir>): ParallelFlux<Pair<ProjectName, License>> {
        val projectName = pair.first
        val licenseDir = pair.second

        return Mono.just(Paths.get(licenseDir, "build", "reports", "license", "license-dependency.html"))
                .map { it.toFile() }
                .filter {
                    if (it.canRead()) true
                    else {
                        log.warn("License file isn't readable for project: {}. Skipping report generation.",
                                projectName)

                        false
                    }
                }
                .flatMapMany {
                    val doc = Jsoup.parse(it, StandardCharsets.UTF_8.name())
                    val t = doc.select("table").first()

                    Mono.justOrEmpty(t)
                }
                .flatMapIterable {
                    it.select("tr")
                            .not(":first-child")
                            .map { row ->
                                val col = row.select("td")

                                if (col.size < 3) {
                                    log.warn("Expected at least 3 columns: Skipping: {}, project: {}.",
                                            row.toString(), projectName)

                                    License()
                                } else {
                                    val name = col[0].text()
                                    val url = col[1].select("a").attr("abs:href")
                                    val components = col[2].select("td.dependencies > ul")
                                            .select("li")
                                            .map { it.text() }

                                    License(name = name, url = url, components = components)
                                }
                            }
                }
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap { license ->
                    license.let { linkVerifier.isValid(it.url).map { valid -> license.copy(valid = valid) } }
                }
                .map { projectName.to(it) }
    }
}