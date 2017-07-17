package org.abhijitsarkar

import org.abhijitsarkar.client.GitLabClient
import org.abhijitsarkar.client.GitLabProperties
import org.abhijitsarkar.client.Group
import org.abhijitsarkar.client.GroupName
import org.abhijitsarkar.service.JGitAgent
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.time.Duration

@RunWith(SpringRunner::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ApplicationTest {
    @Autowired
    lateinit var application: Application

    @MockBean
    lateinit var gitLabClient: GitLabClient

    @MockBean
    lateinit var jGitAgent: JGitAgent

    inline fun <reified T : Any> anyPair() = Mockito.any(Pair::class.java) ?: "".to("")
    inline fun <reified T : Any> anyProject() = Mockito.any(Group.Project::class.java) ?: Group.Project()
    inline fun <reified T : Any> anyGroupProperties() = Mockito.any(GitLabProperties.GroupProperties::class.java)
            ?: GitLabProperties.GroupProperties()

    fun projectDir(): File {
        var projectDir = File(ApplicationTest::class.java.getResource("/").toURI())

        for (i in 1..10) {
            if (projectDir.name == "license-report-kotlin") {
                println("Found project directory.")
                break
            } else {
                projectDir = projectDir.parentFile ?: File("/")
            }
        }

        return projectDir
    }

    @Test
    fun testEndToEnd() {
        var projectDir = projectDir()

        val project = Group.Project().apply {
            sshUrl = ""
            name = "testProject"
        }

        @Suppress("UNCHECKED_CAST")
        Mockito.`when`(gitLabClient.projects(anyPair<Pair<GroupName, GitLabProperties.GroupProperties>>()
                as Pair<GroupName, GitLabProperties.GroupProperties>))
                .thenReturn(Flux.just("testGroup".to(project)))

        Mockito.`when`(jGitAgent.clone(anyProject<Group.Project>(), anyGroupProperties<GitLabProperties.GroupProperties>()))
                .thenReturn(Mono.just(projectDir))

        StepVerifier.create(application.run())
                .assertNext { licenses ->
                    Assert.assertTrue(licenses.size > 0)
                    val containsKotlin = licenses[projectDir.name]?.any { license -> license.components.any { it.contains("kotlin") } }
                            ?: false
                    Assert.assertTrue(containsKotlin)

                    ExcelReportGenerator.generateReport(licenses)
                }
                .expectComplete()
                .verify(Duration.ofMinutes(1L))
    }
}
