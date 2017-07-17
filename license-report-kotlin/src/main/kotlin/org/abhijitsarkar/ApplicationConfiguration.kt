package org.abhijitsarkar

import org.abhijitsarkar.client.GitLabClient
import org.abhijitsarkar.client.GitLabClientImpl
import org.abhijitsarkar.client.GitLabProperties
import org.abhijitsarkar.service.GradleAgent
import org.abhijitsarkar.service.GradleAgentImpl
import org.abhijitsarkar.service.JGitAgent
import org.abhijitsarkar.service.JGitAgentImpl
import org.abhijitsarkar.service.LinkVerifier
import org.abhijitsarkar.service.LinkVerifierImpl
import org.abhijitsarkar.service.ReportParser
import org.abhijitsarkar.service.ReportParserImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Abhijit Sarkar
 */
@Configuration
class ApplicationConfiguration {

    @Bean
    fun gitLabClient(gitLabProperties: GitLabProperties): GitLabClient =
            GitLabClientImpl(gitLabProperties = gitLabProperties)

    @Bean
    fun jGitAgent(): JGitAgent = JGitAgentImpl()

    @Bean
    fun gradleAgent(): GradleAgent = GradleAgentImpl()

    private fun linkVerifier(): LinkVerifier = LinkVerifierImpl.newInstance()

    @Bean
    fun reportParser(): ReportParser = ReportParserImpl(linkVerifier())

    @Bean
    fun excelReportGenerator(): ExcelReportGenerator = ExcelReportGenerator()
}