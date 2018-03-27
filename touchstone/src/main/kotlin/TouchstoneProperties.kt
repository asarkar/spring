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
    var jobName: String = "test-job"
    var restartCompletedStep = false
    var testExecutor = TestExecutor.JUNIT
}