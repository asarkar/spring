package org.abhijitsarkar

import java.io.File

/**
 * @author Abhijit Sarkar
 */
fun projectDir(): File {
    var projectDir = File(object {}::class.java.getResource("/").toURI())

    for (i in 1..10) {
        if (projectDir.name == "license-report-kotlin") {
            println("Found project directory.")
            break
        } else {
            projectDir = projectDir.parentFile ?: File("/")
        }
    }

    return projectDir
}