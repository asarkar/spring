package org.abhijitsarkar.touchstone.execution.junit

import org.junit.platform.console.ConsoleLauncher
import org.junit.platform.console.ConsoleLauncherExecutionResult
import java.io.PrintStream

/**
 * @author Abhijit Sarkar
 */
interface JUnitLauncher {
    fun launch(out: PrintStream, err: PrintStream, args: Array<String>): ConsoleLauncherExecutionResult
}

class DefaultJUnitLauncher : JUnitLauncher {
    override fun launch(out: PrintStream, err: PrintStream, args: Array<String>): ConsoleLauncherExecutionResult {
        return ConsoleLauncher.execute(out, err, *args)
    }
}