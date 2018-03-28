package org.abhijitsarkar.touchstone

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Abhijit Sarkar
 */
enum class TestExecutor {
    JUNIT, GRADLE
}

@ConfigurationProperties("touchstone")
@Component
class TouchstoneProperties {
    var jobName = "TEST_JOB"
    var restartCompletedStep = true
    var testExecutor = TestExecutor.JUNIT
}