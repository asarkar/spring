package org.abhijitsarkar.service

import io.kotlintest.specs.ShouldSpec
import org.abhijitsarkar.client.GitLabProperties
import org.abhijitsarkar.client.Group
import reactor.test.StepVerifier
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
class JGitAgentTest : ShouldSpec() {
    init {
        val jGitAgent = JGitAgentImpl()

        should("clone GitHub project") {
            val project = Group.Project().apply {
                sshUrl = "git@github.com:asarkar/kotlin.git"
                name = "kotlin"
            }

            val groupProperties = GitLabProperties.GroupProperties()

            StepVerifier.create(jGitAgent.clone(project, groupProperties))
                    .assertNext { it.exists() }
                    .expectComplete()
                    .verify(Duration.ofSeconds(3L))
        }
    }
}