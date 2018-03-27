package org.abhijitsarkar.touchstone.execution.junit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author Abhijit Sarkar
 */
@Repository
interface JUnitExecutionSummaryRepository : JpaRepository<JUnitExecutionSummary, TestExecutionId> {
}