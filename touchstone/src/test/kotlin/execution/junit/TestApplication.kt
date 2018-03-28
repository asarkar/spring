package org.abhijitsarkar.touchstone.execution.junit

import org.abhijitsarkar.touchstone.TouchstoneAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Abhijit Sarkar
 */
@SpringBootApplication(exclude = [TouchstoneAutoConfiguration::class])
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}