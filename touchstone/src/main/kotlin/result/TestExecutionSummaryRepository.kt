package org.abhijitsarkar.touchstone.result

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author Abhijit Sarkar
 */
@Repository
interface TestExecutionSummaryRepository : JpaRepository<TestExecutionSummary, TestExecutionId> {
}