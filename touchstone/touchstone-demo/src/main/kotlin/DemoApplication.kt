package org.abhijitsarkar.touchstone.demo

import org.abhijitsarkar.touchstone.condition.Condition
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication
class TouchstoneDemoApplication {
    @Bean
    fun preCondition(): Condition {
        return object : Condition {
            override fun order(): Int {
                return 2
            }

            override fun run(chunkContext: ChunkContext): ExitStatus {
                return ExitStatus.COMPLETED
            }
        }
    }

    @Bean
    fun failingPreCondition(): Condition {
        return object : Condition {
            override fun run(chunkContext: ChunkContext): ExitStatus {
                throw RuntimeException("Boom!")
            }
        }
    }

    @Bean
    fun postCondition(): Condition {
        return object : Condition {
            override fun phase(): Condition.Phase {
                return Condition.Phase.POST
            }

            override fun run(chunkContext: ChunkContext): ExitStatus {
                return ExitStatus.COMPLETED
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TouchstoneDemoApplication>(*args)
}