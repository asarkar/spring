package org.abhijitsarkar.service

import com.jcraft.jsch.Session
import org.abhijitsarkar.client.GitLabProperties
import org.abhijitsarkar.client.Group
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files

/**
 * @author Abhijit Sarkar
 */

interface JGitAgent {
    fun clone(project: Group.Project, group: GitLabProperties.GroupProperties?): Mono<File>
}

internal class JGitAgentImpl : JGitAgent {
    init {
        SshSessionFactory.setInstance(object : JschConfigSessionFactory() {
            override fun configure(hc: OpenSshConfig.Host, session: Session) {
                session.setConfig("StrictHostKeyChecking", "no")
            }
        })
    }

    private val log = LoggerFactory.getLogger(JGitAgent::class.java)

    override fun clone(project: Group.Project, group: GitLabProperties.GroupProperties?): Mono<File> {
        if (group == null) return Mono.empty<File>()

        return Mono.create<File> { consumer ->
            var git: Git? = null
            val branch = group
                    .includedProjects
                    .getOrDefault(project.name, GitLabProperties.ProjectProperties())
                    .branch

            try {
                val projectDir = Files.createTempDirectory(null).toFile()
                        .apply { deleteOnExit() }
                        .let { File(it, project.name) }

                log.info("Cloning repo: {} and branch: {} to: {}.",
                        project.sshUrl, branch, projectDir.absolutePath)

                git = Git.cloneRepository()
                        .setURI(project.sshUrl)
                        .setDirectory(projectDir)
                        .setCloneAllBranches(false)
                        .setBranch(branch)
                        .call()

                consumer.success(projectDir)
            } catch (e: Exception) {
                log.error("Failed to clone repo: {}.", project.sshUrl, e)

                consumer.error(e)
            } finally {
                git?.close()
            }
        }
                .retry(2)
                .onErrorResume { _ -> Mono.empty() }
    }
}