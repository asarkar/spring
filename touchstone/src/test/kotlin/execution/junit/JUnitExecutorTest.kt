package org.abhijitsarkar.touchstone.execution.junit

import org.abhijitsarkar.touchstone.mockito.any
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.platform.console.ConsoleLauncherExecutionResult
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus

/**
 * @author Abhijit Sarkar
 */
class JUnitExecutorTest {
    private var jUnitProperties = JUnitProperties()

    private lateinit var junitLauncher: JUnitLauncher
    private lateinit var junitExecutor: JUnitExecutor
    private lateinit var chunkContext: ChunkContext
    private lateinit var executionContext: ExecutionContext
    private lateinit var result: ConsoleLauncherExecutionResult
    private lateinit var contribution: StepContribution

    @BeforeEach
    fun beforeEach() {
        junitLauncher = mock(JUnitLauncher::class.java)
        chunkContext = mock(ChunkContext::class.java, RETURNS_DEEP_STUBS)
        executionContext = mock(ExecutionContext::class.java)
        `when`(chunkContext.stepContext.stepExecution.executionContext).thenReturn(executionContext)
        junitExecutor = JUnitExecutor(jUnitProperties, junitLauncher)
        result = mock(ConsoleLauncherExecutionResult::class.java)
        contribution = mock(StepContribution::class.java)
        `when`(junitLauncher.launch(any(), any(), any()))
                .thenAnswer {
                    val args = it.getArgument<Array<String>>(2)
                    println(args.contentToString())
                    result
                }
    }

    @Test
    fun `should complete`() {
        `when`(result.exitCode).thenReturn(0)

        val status = junitExecutor.execute(contribution, chunkContext)
        Assertions.assertThat(status).isEqualTo(RepeatStatus.FINISHED)
        verify(executionContext).put(ArgumentMatchers.eq(JUnitExecutor.EXECUTION_RESULT_KEY), any())
        verify(contribution, never()).exitStatus = any()
    }

    @Test
    fun `should fail`() {
        `when`(result.exitCode).thenReturn(1)

        val status = junitExecutor.execute(contribution, chunkContext)
        Assertions.assertThat(status).isEqualTo(RepeatStatus.FINISHED)
        verify(executionContext).put(ArgumentMatchers.eq(JUnitExecutor.EXECUTION_RESULT_KEY), any())
        verify(contribution).exitStatus = ExitStatus.FAILED
    }
}