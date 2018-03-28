package org.abhijitsarkar.touchstone

import org.abhijitsarkar.touchstone.condition.Condition
import org.abhijitsarkar.touchstone.condition.ConditionsExecutionListener
import org.abhijitsarkar.touchstone.condition.ConditionsExecutor
import org.abhijitsarkar.touchstone.execution.TestExecutorDecider
import org.abhijitsarkar.touchstone.execution.gradle.GradleAgentImpl
import org.abhijitsarkar.touchstone.execution.gradle.GradleExecutor
import org.abhijitsarkar.touchstone.execution.gradle.GradleProperties
import org.abhijitsarkar.touchstone.execution.junit.DefaultJUnitLauncher
import org.abhijitsarkar.touchstone.execution.junit.JUnitExecutionListener
import org.abhijitsarkar.touchstone.execution.junit.JUnitExecutionSummary
import org.abhijitsarkar.touchstone.execution.junit.JUnitExecutionSummaryRepository
import org.abhijitsarkar.touchstone.execution.junit.JUnitExecutor
import org.abhijitsarkar.touchstone.execution.junit.JUnitLauncher
import org.abhijitsarkar.touchstone.execution.junit.JUnitProperties
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.support.SimpleFlow
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


/**
 * @author Abhijit Sarkar
 */
@Configuration
@ComponentScan
@EntityScan(basePackageClasses = [JUnitExecutionSummary::class])
@EnableJpaRepositories(basePackageClasses = [JUnitExecutionSummaryRepository::class])
@EnableBatchProcessing
@PropertySource("classpath:/touchstone.properties")
class TouchstoneAutoConfiguration(
        private val jobs: JobBuilderFactory,
        private val steps: StepBuilderFactory,
        private val touchstoneProperties: TouchstoneProperties,
        private val junitProperties: JUnitProperties,
        private val gradleProperties: GradleProperties,
        private val repo: JUnitExecutionSummaryRepository,
        private val env: Environment
) {
    @ConditionalOnMissingBean(JUnitLauncher::class)
    @Bean
    fun junitLauncher(): JUnitLauncher = DefaultJUnitLauncher()

    @Bean
    fun junitExecutionStep(): Step {
        return steps.get(TestExecutor.JUNIT.name)
                .tasklet(JUnitExecutor(junitProperties, junitLauncher()))
                .allowStartIfComplete(touchstoneProperties.restartCompletedStep)
                .listener(JUnitExecutionListener(repo))
                .build()
    }

    @Bean
    fun gradleExecutionStep(): Step {
        return steps.get(TestExecutor.GRADLE.name)
                .tasklet(GradleExecutor(GradleAgentImpl(gradleProperties)))
                .allowStartIfComplete(touchstoneProperties.restartCompletedStep)
                .build()
    }

    @Bean
    fun preConditionsStep(): Step {
        return conditionsStep(Condition.Phase.PRE)
    }

    @Bean
    fun postConditionsStep(): Step {
        return conditionsStep(Condition.Phase.POST)
    }

    @Bean
    fun conditionsExecutor() = ConditionsExecutor(env)

    private fun conditionsStep(phase: Condition.Phase): Step {
        return steps.get(phase.name)
                .tasklet(conditionsExecutor())
                .allowStartIfComplete(touchstoneProperties.restartCompletedStep)
                .listener(ConditionsExecutionListener(phase))
                .build()
    }

    @Bean
    fun job(): Job {
        val testExecutorDecider = TestExecutorDecider(touchstoneProperties)
        val testingFlow = FlowBuilder<SimpleFlow>("TESTING_FLOW")
                .start(testExecutorDecider)
                .on(TestExecutor.JUNIT.name).to(junitExecutionStep())
                .from(testExecutorDecider).on(TestExecutor.GRADLE.name).to(gradleExecutionStep())
                .end()

        return jobs.get(touchstoneProperties.jobName)
                .flow(preConditionsStep())
                .next(testingFlow)
                .on("*").to(postConditionsStep())
                .end()
                .build()
    }
}
