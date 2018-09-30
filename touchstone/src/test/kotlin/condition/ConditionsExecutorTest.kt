package org.abhijitsarkar.touchstone.condition

import org.abhijitsarkar.touchstone.mockito.any
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import java.util.Locale

/**
 * @author Abhijit Sarkar
 */
class ConditionsExecutorTest {
    private val preCondition1 = Mockito.mock(Condition::class.java, "pre-condition-1")
    private val preCondition2 = Mockito.mock(Condition::class.java, "pre-condition-2")
    private val postCondition1 = Mockito.mock(Condition::class.java, "post-condition-1")
    private val postCondition2 = Mockito.mock(Condition::class.java, "post-condition-2")
    private val chunkContext = Mockito.mock(ChunkContext::class.java, Mockito.RETURNS_DEEP_STUBS)
    private val env = Mockito.mock(ConfigurableEnvironment::class.java)
    private val executor = ConditionsExecutor(env)
    private lateinit var contribution: StepContribution

    @BeforeEach
    fun beforeEach() {
        Mockito.`when`(preCondition1.phase()).thenReturn(Condition.Phase.PRE)
        Mockito.`when`(preCondition2.phase()).thenReturn(Condition.Phase.PRE)
        Mockito.`when`(postCondition1.phase()).thenReturn(Condition.Phase.POST)
        Mockito.`when`(postCondition2.phase()).thenReturn(Condition.Phase.POST)

        val conditions = listOf(preCondition1, preCondition2, postCondition1, postCondition2)
        conditions.forEach {
            Mockito.`when`(it.run(any())).thenReturn(ExitStatus.COMPLETED)
            Mockito.`when`(it.shouldRun()).thenReturn(true)
            Mockito.`when`(it.qualifiedName).thenAnswer { invocation ->
                (invocation.mock as Condition).let {
                    listOf(Condition.PREFIX, it.phase().name, Mockito.mockingDetails(invocation.mock).mockCreationSettings.mockName.toString())
                            .joinToString(separator = ".") { it.toLowerCase(Locale.ENGLISH) }
                }
            }
        }

        val map = mapOf(
                "${preCondition1.qualifiedName}.order" to "2",
                "${postCondition1.qualifiedName}.order" to "2",
                "${postCondition2.qualifiedName}.should-run" to "false"
        )
        val propertySources = MutablePropertySources().apply {
            addFirst(MapPropertySource("test", map))
        }
        Mockito.`when`(env.propertySources).thenReturn(propertySources)
        map.forEach {
            Mockito.`when`(env.getProperty(eq(it.key), eq(Any::class.java))).thenReturn(it.value)
        }

        executor.conditions = conditions
        executor.postConstruct()
        contribution = Mockito.mock(StepContribution::class.java)
    }

    @Test
    fun `should run pre conditions`() {
        val inOrder = inOrder(preCondition2, preCondition1)
        `when`(chunkContext.stepContext.stepExecution.executionContext[ConditionsExecutor.CONDITION_PHASE_KEY])
                .thenReturn(Condition.Phase.PRE)

        val status = executor.execute(contribution, chunkContext)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)

        inOrder.verify(preCondition2).run(chunkContext)
        inOrder.verify(preCondition1).run(chunkContext)

        verify(postCondition1, never()).run(any())
        verify(postCondition2, never()).run(any())
    }

    @Test
    fun `should run post conditions`() {
        `when`(chunkContext.stepContext.stepExecution.executionContext[ConditionsExecutor.CONDITION_PHASE_KEY])
                .thenReturn(Condition.Phase.POST)

        val status = executor.execute(contribution, chunkContext)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)

        verify(preCondition1, never()).run(any())
        verify(preCondition2, never()).run(any())
        verify(postCondition2, never()).run(any())
        verify(postCondition1).run(chunkContext)
    }
}