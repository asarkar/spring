package org.abhijitsarkar.touchstone.condition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources

/**
 * @author Abhijit Sarkar
 */
class ConditionsExecutorTest {
    private val preCondition1 = Mockito.mock(Condition::class.java)

    private val preCondition2 = Mockito.mock(Condition::class.java)

    private val postCondition1 = Mockito.mock(Condition::class.java)

    private val postCondition2 = Mockito.mock(Condition::class.java)

    private val map = mapOf(
            "touchstone.condition.pre.preCondition1.order" to "2",
            "touchstone.condition.post.postCondition1.order" to "2",
            "touchstone.condition.post.postCondition2.shouldRun" to "false"
    )
    private val propertySources = MutablePropertySources().apply {
        addFirst(MapPropertySource("test", map))
    }

    private val conditions = listOf(preCondition1, preCondition2, postCondition1, postCondition2)

    private lateinit var env: ConfigurableEnvironment

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T

    @BeforeEach
    fun beforeEach() {
        env = Mockito.mock(ConfigurableEnvironment::class.java)
        Mockito.`when`(env.propertySources).thenReturn(propertySources)
        map.forEach {
            Mockito.`when`(env.getProperty(eq(it.key), eq(Any::class.java))).thenReturn(it.value)
        }
        Mockito.`when`(preCondition1.phase()).thenReturn(Condition.Phase.PRE)
        Mockito.`when`(preCondition1.qualifiedName).thenReturn("touchstone.condition.pre.preCondition1")
        Mockito.`when`(preCondition2.phase()).thenReturn(Condition.Phase.PRE)
        Mockito.`when`(preCondition2.qualifiedName).thenReturn("touchstone.condition.pre.preCondition2")
        Mockito.`when`(postCondition1.phase()).thenReturn(Condition.Phase.POST)
        Mockito.`when`(postCondition1.qualifiedName).thenReturn("touchstone.condition.post.postCondition1")
        Mockito.`when`(postCondition2.phase()).thenReturn(Condition.Phase.POST)
        Mockito.`when`(postCondition2.qualifiedName).thenReturn("touchstone.condition.post.postCondition2")
        conditions.forEach {
            Mockito.`when`(it.run(any())).thenReturn(ExitStatus.COMPLETED)
            Mockito.`when`(it.shouldRun()).thenReturn(true)
        }
    }

    @Test
    fun `should run pre conditions`() {
        val inOrder = inOrder(preCondition2, preCondition1)
        val executor = ConditionsExecutor(Condition.Phase.PRE, env)
        executor.conditions = conditions
        executor.postConstruct()

        val chunkContext = Mockito.mock(ChunkContext::class.java)
        val status = executor.execute(null, chunkContext)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)

        inOrder.verify(preCondition2).run(chunkContext)
        inOrder.verify(preCondition1).run(chunkContext)

        verify(postCondition1, never()).run(chunkContext)
        verify(postCondition2, never()).run(chunkContext)
    }

    @Test
    fun `should run post conditions`() {
        val executor = ConditionsExecutor(Condition.Phase.POST, env)
        executor.conditions = conditions
        executor.postConstruct()

        val chunkContext = Mockito.mock(ChunkContext::class.java)
        val status = executor.execute(null, chunkContext)

        assertThat(status).isEqualTo(RepeatStatus.FINISHED)

        verify(preCondition1, never()).run(any())
        verify(preCondition2, never()).run(any())
        verify(postCondition2, never()).run(any())
        verify(postCondition1).run(chunkContext)
    }
}