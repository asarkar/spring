package org.abhijitsarkar.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * @author Abhijit Sarkar
 */
@ConfigurationProperties("licensereport.gitlab")
class GitLabProperties {
    lateinit var baseUrl: String

    @NestedConfigurationProperty
    val groups = mutableMapOf<String, GroupProperties>()

    @NestedConfigurationProperty
    val connection = ConnectionProperties()

    class GroupProperties {
        lateinit var privateToken: String
        val excludedProjects = mutableMapOf<String, ProjectProperties>()
        val includedProjects = mutableMapOf<String, ProjectProperties>()
    }

    class ProjectProperties {
        var branch = "master"
    }

    class ConnectionProperties {
        var connectTimeoutMillis = 500
        var readTimeoutMillis = 5000
    }
}
