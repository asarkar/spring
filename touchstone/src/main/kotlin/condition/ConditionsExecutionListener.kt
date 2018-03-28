package org.abhijitsarkar.touchstone.condition

import org.abhijitsarkar.touchstone.condition.ConditionsExecutor.Companion.CONDITION_PHASE_KEY
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.listener.StepExecutionListenerSupport

/**
 * @author Abhijit Sarkar
 */
class ConditionsExecutionListener(private val phase: Condition.Phase) : StepExecutionListenerSupport() {
    override fun beforeStep(stepExecution: StepExecution) {
        stepExecution.executionContext.put(CONDITION_PHASE_KEY, phase)
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        stepExecution.executionContext.remove(CONDITION_PHASE_KEY)

        return stepExecution.exitStatus
    }
}