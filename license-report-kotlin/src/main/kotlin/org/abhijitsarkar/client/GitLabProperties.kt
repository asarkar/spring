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
    var groups = mutableMapOf<String, GroupProperties>()

    @NestedConfigurationProperty
    var connection = ConnectionProperties()

    class GroupProperties {
        lateinit var privateToken: String
        var excludedProjects = mutableMapOf<String, ProjectProperties>()
        var includedProjects = mutableMapOf<String, ProjectProperties>()
    }

    class ProjectProperties {
        var branch = "master"
    }

    class ConnectionProperties {
        var connectTimeoutMillis = 500
        var readTimeoutMillis = 5000
    }
}
