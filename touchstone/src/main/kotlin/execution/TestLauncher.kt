package org.abhijitsarkar.touchstone.execution

import org.junit.platform.console.ConsoleLauncher
import org.junit.platform.console.ConsoleLauncherExecutionResult
import java.io.PrintStream

/**
 * @author Abhijit Sarkar
 */
interface TestLauncher {
    fun launch(out: PrintStream, err: PrintStream, args: Array<String>): ConsoleLauncherExecutionResult
}

class DefaultTestLauncher : TestLauncher {
    override fun launch(out: PrintStream, err: PrintStream, args: Array<String>): ConsoleLauncherExecutionResult {
        return ConsoleLauncher.execute(out, err, *args)
    }
}