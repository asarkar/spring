package org.abhijitsarkar.touchstone.execution.junit

import org.abhijitsarkar.touchstone.execution.junit.JUnitExecutor.Companion.EXECUTION_RESULT_KEY
import org.junit.platform.console.ConsoleLauncherExecutionResult
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.listener.StepExecutionListenerSupport

/**
 * @author Abhijit Sarkar
 */
class JUnitExecutionListener(private val repo: JUnitExecutionSummaryRepository) : StepExecutionListenerSupport() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(JUnitExecutionListener::class.java)
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
            val summary = JUnitExecutionSummary.fromJUnit(junit).apply {
                id = testExecutionId
            }

            LOGGER.debug("Test execution summary: {}", summary)

            try {
                repo.save(summary)
            } catch (e: Exception) {
                LOGGER.error("Failed to save test execution summary for: $testExecutionId", e)
            }
        }

        stepExecution.executionContext.remove(EXECUTION_RESULT_KEY)

        return stepExecution.exitStatus
    }
}