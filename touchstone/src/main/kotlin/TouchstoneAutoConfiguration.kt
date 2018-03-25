package org.abhijitsarkar.touchstone

import org.abhijitsarkar.touchstone.execution.TestExecutionListener
import org.abhijitsarkar.touchstone.execution.TestExecutor
import org.abhijitsarkar.touchstone.precondition.Teller
import org.abhijitsarkar.touchstone.result.TestExecutionSummary
import org.abhijitsarkar.touchstone.result.TestExecutionSummaryRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


/**
 * @author Abhijit Sarkar
 */
@Configuration
@ComponentScan
@EntityScan(basePackageClasses = [TestExecutionSummary::class])
@EnableJpaRepositories(basePackageClasses = [TestExecutionSummaryRepository::class])
@EnableBatchProcessing
@PropertySource("classpath:/touchstone.properties")
class TouchstoneAutoConfiguration(
        private val jobs: JobBuilderFactory,
        private val steps: StepBuilderFactory,
        private val touchstoneProperties: TouchstoneProperties,
        private val repo: TestExecutionSummaryRepository
) {
    @Bean
    fun tellerStep(): Step {
        return steps.get("count-votes")
                .tasklet(teller())
                .allowStartIfComplete(touchstoneProperties.restartCompleted)
                .build()
    }

    @Bean
    fun teller() = Teller(touchstoneProperties)

    @Bean
    fun testExecutionStep(): Step {
        return steps.get("execute-tests")
                .tasklet(testExecutor())
                .allowStartIfComplete(touchstoneProperties.restartCompleted)
                .listener(TestExecutionListener(repo))
                .build()
    }

    @Bean
    fun testExecutor() = TestExecutor(touchstoneProperties)

    @Bean
    fun job(): Job {
        return jobs.get(touchstoneProperties.jobName)
                .start(tellerStep())
                .next(testExecutionStep())
                .build()
    }
}
