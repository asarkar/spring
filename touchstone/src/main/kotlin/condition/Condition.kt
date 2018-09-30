package org.abhijitsarkar.touchstone.condition

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.scope.context.ChunkContext
import java.util.Locale

/**
 * @author Abhijit Sarkar
 */
fun String.toKebabCase() = this.split("(?=\\p{Upper})".toRegex()).joinToString("-")

interface Condition {
    companion object {
        const val PREFIX = "touchstone.condition"
    }

    enum class Phase {
        PRE, POST
    }

    fun run(chunkContext: ChunkContext): ExitStatus

    fun phase(): Phase = Phase.PRE

    fun order(): Int = 1

    fun shouldRun(): Boolean = true

    val qualifiedName: String
        get() = listOf(PREFIX, phase().name, javaClass.simpleName.replace("\\W".toRegex(), "-").toKebabCase())
                .joinToString(separator = ".") { it.toLowerCase(Locale.ENGLISH) }
}