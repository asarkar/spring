package org.abhijitsarkar.touchstone.execution

import org.abhijitsarkar.touchstone.execution.TestExecutor.Companion.EXECUTION_RESULT_KEY
import org.abhijitsarkar.touchstone.result.TestExecutionId
import org.abhijitsarkar.touchstone.result.TestExecutionSummary
import org.abhijitsarkar.touchstone.result.TestExecutionSummaryRepository
import org.junit.platform.console.ConsoleLauncherExecutionResult
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.listener.StepExecutionListenerSupport

/**
 * @author Abhijit Sarkar
 */
class TestExecutionListener(private val repo: TestExecutionSummaryRepository) : StepExecutionListenerSupport() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestExecutionListener::class.java)
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        val result = stepExecution.executionContext.get(EXECUTION_RESULT_KEY) as ConsoleLauncherExecutionResult
        val testExecutionId = TestExecutionId(
                stepExecution.id,
                stepExecution.jobExecutionId
        )
        if (result.exitCode != 0) {
            LOGGER.error("Test execution failed for: {}", testExecutionId)
        }
        result.testExecutionSummary.ifPresent { junit ->
            val summary = TestExecutionSummary.fromJUnit(junit).apply {
                id = testExecutionId
            }

            LOGGER.debug("Test execution summary: {}", summary)

            try {
                repo.save(summary)
            } catch (e: Exception) {
                LOGGER.error("Failed to save test execution summary for: $testExecutionId", e)
            } finally {
                stepExecution.executionContext.remove(EXECUTION_RESULT_KEY)
            }
        }

        return stepExecution.exitStatus
    }
}