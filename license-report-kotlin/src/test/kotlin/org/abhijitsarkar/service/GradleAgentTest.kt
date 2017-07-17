package org.abhijitsarkar.service

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.abhijitsarkar.projectDir
import reactor.test.StepVerifier
import java.io.File
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
class GradleAgentTest : ShouldSpec() {
    init {
        val gradleAgent = GradleAgentImpl()
        val projectDir = projectDir()
        val testProjectDir = File(projectDir(), "test-project")

        should("recognize Gradle project") {
            gradleAgent.isGradleProject(testProjectDir) shouldBe true
        }

        should("extract Gradle distribution URL") {
            StepVerifier.create(gradleAgent.gradleDistroUrl(projectDir))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify(Duration.ofSeconds(3L))
        }

        should("execute downloadLicenses task") {
            StepVerifier.create(gradleAgent.generateLicense(testProjectDir, ""))
                    .assertNext { it.second shouldBe testProjectDir.absolutePath }
                    .expectComplete()
                    .verify(Duration.ofSeconds(3L))
        }
    }
}