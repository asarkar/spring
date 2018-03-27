package org.abhijitsarkar.touchstone.execution.gradle

import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.nio.file.Paths

/**
 * @author Abhijit Sarkar
 */
class GradleExecutor(private val gradleAgent: GradleAgent) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GradleExecutor::class.java)
    }

    private val projectDir = javaClass.protectionDomain.codeSource.location
            .let {
                LOGGER.debug("Searching for Gradle project from URL: {}", it)
                generateSequence(Paths.get(it.path).toAbsolutePath()) {
                    it.parent
                }
                        .firstOrNull { GradleAgent.isGradleProject(it) }
            }

    override fun execute(contribution: StepContribution?, chunkContext: ChunkContext?): RepeatStatus {
        if (projectDir != null) {
            LOGGER.info("Found Gradle project directory: {}", projectDir)
            gradleAgent.build(projectDir)
        } else {
            LOGGER.warn("Gradle project not found")
        }

        return RepeatStatus.FINISHED
    }
}