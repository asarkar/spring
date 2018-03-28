package org.abhijitsarkar.touchstone.condition

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.scope.context.ChunkContext
import java.util.Locale

/**
 * @author Abhijit Sarkar
 */
enum class Vote {
    READY, NOT_READY, ABSTAIN;
}

interface Condition {
    enum class Phase {
        PRE, POST
    }

    fun run(chunkContext: ChunkContext): ExitStatus

    fun phase(): Phase = Phase.PRE

    fun order(): Int = 1

    fun shouldRun(): Boolean = true

    val qualifiedName: String
        get() = "touchstone.condition.${phase().name.toLowerCase(Locale.ENGLISH)}.${javaClass.simpleName}"
}