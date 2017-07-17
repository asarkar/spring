package org.abhijitsarkar

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("licensereport")
class ApplicationProperties {
    var timeoutMinutes: Long = 30L

    @NestedConfigurationProperty
    var gradle = GradleProperties()

    class GradleProperties {
        var options = ""
    }
}