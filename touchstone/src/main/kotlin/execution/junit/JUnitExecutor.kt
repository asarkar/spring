package org.abhijitsarkar.touchstone.execution.junit

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * @author Abhijit Sarkar
 */
class TestFailedException(override val message: String? = null, val exitCode: Int) : RuntimeException(message)

class JUnitExecutor(
        private val junit: JUnitProperties,
        private val junitLauncher: JUnitLauncher
) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(JUnitExecutor::class.java)
        const val EXECUTION_RESULT_KEY = "touchstone.junit.executionResult"
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val args = mutableListOf<String>()

        if (junit.help)
            args += "--help"
        if (junit.disableAnsiColors)
            args += "--disable-ansi-colors"

        args += "--details"
        args += junit.details.name.toLowerCase(Locale.ENGLISH)
        args += "--details-theme"
        args += junit.detailsTheme.name.toLowerCase(Locale.ENGLISH)
        args += "--reports-dir"
        args += junit.reportsDir

        junit.selectUri.forEach {
            args += "--select-uri"
            args += it
        }

        junit.selectFile.forEach {
            args += "--select-file"
            args += it
        }

        junit.selectDirectory.forEach {
            args += "--select-directory"
            args += it
        }

        junit.selectPackage.forEach {
            args += "--select-package"
            args += it
        }

        junit.selectClass.forEach {
            args += "--select-class"
            args += it
        }

        junit.selectMethod.forEach {
            args += "--select-method"
            args += it
        }

        junit.selectResource.forEach {
            args += "--select-resource"
            args += it
        }

        junit.includeClassname.forEach {
            args += "--include-classname"
            args += it
        }

        junit.excludeClassname.forEach {
            args += "--exclude-classname"
            args += it
        }

        junit.includePackage.forEach {
            args += "--include-package"
            args += it
        }

        junit.excludePackage.forEach {
            args += "--exclude-package"
            args += it
        }

        junit.includeTag.forEach {
            args += "--include-tag"
            args += it
        }

        junit.excludeTag.forEach {
            args += "--exclude-tag"
            args += it
        }

        junit.config.forEach {
            args += "--config"
            args += "${it.key}=${it.value}"
        }

        LOGGER.info("Test args: {}", args)

        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        val p1 = PrintStream(out)
        val p2 = PrintStream(err)

        val result = junitLauncher.launch(p1, p2, args.toTypedArray())

        if (out.size() > 0) {
            LOGGER.info("{}", out.toString(StandardCharsets.UTF_8.name()))
        }
        if (err.size() > 0) {
            LOGGER.error("{}", err.toString(StandardCharsets.UTF_8.name()))
        }
        p1.close()
        p2.close()

        chunkContext.stepContext.stepExecution.executionContext.put(EXECUTION_RESULT_KEY, result)

        if (result.exitCode != 0) {
            contribution.exitStatus = ExitStatus.FAILED
        }

        return RepeatStatus.FINISHED
    }
}