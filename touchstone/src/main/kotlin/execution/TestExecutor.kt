package org.abhijitsarkar.touchstone.execution

import org.abhijitsarkar.touchstone.TouchstoneProperties
import org.slf4j.LoggerFactory
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

class TestExecutor(
        touchstoneProperties: TouchstoneProperties,
        private val testLauncher: TestLauncher = DefaultTestLauncher()
) : Tasklet {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestExecutor::class.java)
        const val EXECUTION_RESULT_KEY = "touchstone.test.executionResult"
    }

    private val test = touchstoneProperties.test

    override fun execute(contribution: StepContribution?, chunkContext: ChunkContext): RepeatStatus {
        val args = mutableListOf<String>()

        if (test.help)
            args += "--help"
        if (test.disableAnsiColors)
            args += "--disable-ansi-colors"

        args += "--details"
        args += test.details.name.toLowerCase(Locale.ENGLISH)
        args += "--details-theme"
        args += test.detailsTheme.name.toLowerCase(Locale.ENGLISH)
        args += "--reports-dir"
        args += test.reportsDir

        test.selectUri.forEach {
            args += "--select-uri"
            args += it
        }

        test.selectFile.forEach {
            args += "--select-file"
            args += it
        }

        test.selectDirectory.forEach {
            args += "--select-directory"
            args += it
        }

        test.selectPackage.forEach {
            args += "--select-package"
            args += it
        }

        test.selectClass.forEach {
            args += "--select-class"
            args += it
        }

        test.selectMethod.forEach {
            args += "--select-method"
            args += it
        }

        test.selectResource.forEach {
            args += "--select-resource"
            args += it
        }

        test.includeClassname.forEach {
            args += "--include-classname"
            args += it
        }

        test.excludeClassname.forEach {
            args += "--exclude-classname"
            args += it
        }

        test.includePackage.forEach {
            args += "--include-package"
            args += it
        }

        test.excludePackage.forEach {
            args += "--exclude-package"
            args += it
        }

        test.includeTag.forEach {
            args += "--include-tag"
            args += it
        }

        test.excludeTag.forEach {
            args += "--exclude-tag"
            args += it
        }

        test.config.forEach {
            args += "--config"
            args += "${it.key}=${it.value}"
        }

        LOGGER.info("Test args: {}", args)

        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        val p1 = PrintStream(out)
        val p2 = PrintStream(err)

        val result = testLauncher.launch(p1, p2, args.toTypedArray())

        if (out.size() > 0) {
            LOGGER.info("{}", out.toString(StandardCharsets.UTF_8.name()))
        }
        if (err.size() > 0) {
            LOGGER.error("{}", err.toString(StandardCharsets.UTF_8.name()))
        }
        p1.close()
        p2.close()

        chunkContext.stepContext.stepExecution.executionContext.put(EXECUTION_RESULT_KEY, result)

        return if (result.exitCode == 0)
            RepeatStatus.FINISHED
        else
            throw TestFailedException(exitCode = result.exitCode)
    }
}