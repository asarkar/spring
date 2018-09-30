package org.abhijitsarkar.touchstone.condition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.scope.context.ChunkContext

/**
 * @author Abhijit Sarkar
 */
class ConditionTest {
    @Test
    fun testQualifiedName() {
        val preCondition = object : Condition {
            override fun run(chunkContext: ChunkContext): ExitStatus {
                TODO("not implemented")
            }
        }

        assertThat(preCondition.qualifiedName).isEqualTo("${Condition.PREFIX}.pre.test-qualified-name-pre-condition-1")
    }
}