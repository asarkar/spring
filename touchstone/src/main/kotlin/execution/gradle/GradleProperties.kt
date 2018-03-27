package org.abhijitsarkar.touchstone.execution.gradle

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Abhijit Sarkar
 */
@Component
@ConfigurationProperties("touchstone.gradle")
class GradleProperties {
    var tasks: Array<String> = arrayOf("test")
    var options: Array<String> = arrayOf("--warn")
}