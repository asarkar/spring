package org.abhijitsarkar.touchstone.precondition

import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider

/**
 * @author Abhijit Sarkar
 */
class VotingDecider(private val votingProperties: VotingProperties) : JobExecutionDecider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(VotingDecider::class.java)
    }

    override fun decide(jobExecution: JobExecution?, stepExecution: StepExecution?): FlowExecutionStatus {
        return if (votingProperties.skip) {
            LOGGER.warn("Voting is skipped")
            FlowExecutionStatus("SKIPPED")
        } else
            FlowExecutionStatus("CONTINUE")
    }
}