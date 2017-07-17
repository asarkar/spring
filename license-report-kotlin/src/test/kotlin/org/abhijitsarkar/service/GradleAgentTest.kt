package org.abhijitsarkar.service

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import reactor.test.StepVerifier
import java.io.File
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
class GradleAgentTest : ShouldSpec() {
    fun projectDir(): File {
        var projectDir = File(GradleAgentTest::class.java.getResource("/").toURI())

        for (i in 1..10) {
            if (projectDir.name == "license-report-kotlin") {
                println("Found project directory.")
                break
            } else {
                projectDir = projectDir.parentFile ?: File("/")
            }
        }

        return projectDir
    }

    init {
        val gradleAgent = GradleAgentImpl("clean")
        var projectDir = projectDir()

        should("recognize Gradle project") {
            gradleAgent.isGradleProject(projectDir) shouldBe true
        }

        should("extract Gradle distribution URL") {
            StepVerifier.create(gradleAgent.gradleDistroUrl(projectDir))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify(Duration.ofSeconds(3L))
        }

        should("execute clean task") {
            StepVerifier.create(gradleAgent.generateLicense(projectDir, ""))
                    .assertNext { it.second shouldBe projectDir.absolutePath }
                    .expectComplete()
                    .verify(Duration.ofSeconds(3L))
        }
    }
}