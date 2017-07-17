package org.abhijitsarkar.client;

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Abhijit Sarkar
 */
class Group {
    lateinit var name: String
    var projects = emptyList<Project>()

    class Project {
        @JsonProperty("ssh_url_to_repo")
        lateinit var sshUrl: String
        lateinit var name: String
    }
}
