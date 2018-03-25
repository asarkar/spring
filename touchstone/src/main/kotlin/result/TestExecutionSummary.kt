package org.abhijitsarkar.touchstone.result

import org.springframework.beans.BeanUtils
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import org.junit.platform.launcher.listeners.TestExecutionSummary as JUnitSummary

/**
 * @author Abhijit Sarkar
 */
@Entity
class TestExecutionSummary : Serializable {
    companion object {
        fun fromJUnit(junit: JUnitSummary): TestExecutionSummary {
            return TestExecutionSummary().apply {
                timeStarted = Instant
                        .ofEpochMilli(junit.timeStarted)
                        .atOffset(ZoneOffset.UTC)
                timeFinished = Instant
                        .ofEpochMilli(junit.timeFinished)
                        .atOffset(ZoneOffset.UTC)
                BeanUtils.copyProperties(junit, this)
                failures = junit.failures
                        .map {
                            TestFailure().also { tf ->
                                tf.testId = it.testIdentifier.uniqueId
                                tf.message = it.exception.message
                            }
                        }
            }
        }
    }

    @EmbeddedId
    lateinit var id: TestExecutionId

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    lateinit var timeStarted: OffsetDateTime
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    lateinit var timeFinished: OffsetDateTime
    var totalFailureCount = 0L
    var containersFoundCount = 0L
    var containersStartedCount = 0L
    var containersSkippedCount = 0L
    var containersAbortedCount = 0L
    var containersSucceededCount = 0L
    var containersFailedCount = 0L
    var testsFoundCount = 0L
    var testsStartedCount = 0L
    var testsSkippedCount = 0L
    var testsAbortedCount = 0L
    var testsSucceededCount = 0L
    var testsFailedCount = 0L
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "TEST_FAILURE",
            joinColumns = [
                JoinColumn(name = "stepExecutionId", referencedColumnName = "stepExecutionId"),
                JoinColumn(name = "jobExecutionId", referencedColumnName = "jobExecutionId")
            ]
    )
    var failures: List<TestFailure> = mutableListOf()
}

@Embeddable
data class TestExecutionId(
        var stepExecutionId: Long,
        var jobExecutionId: Long
) : Serializable

@Embeddable
class TestFailure : Serializable {
    @Column(length = 999)
    lateinit var testId: String
    @Column(length = 999)
    var message: String? = null
}