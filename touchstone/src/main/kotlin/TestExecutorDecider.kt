package org.abhijitsarkar.touchstone

import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider

/**
 * @author Abhijit Sarkar
 */
class TestExecutorDecider(private val touchstoneProperties: TouchstoneProperties) : JobExecutionDecider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestExecutorDecider::class.java)
    }

    override fun decide(jobExecution: JobExecution?, stepExecution: StepExecution?): FlowExecutionStatus {
        LOGGER.info("Test executor: {}", touchstoneProperties.testExecutor)
        return FlowExecutionStatus(touchstoneProperties.testExecutor.name)
    }
}