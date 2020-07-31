pluginManagement {
    val kotlinPluginVersion: String by settings
    val buildTimeTrackerPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinPluginVersion
        id("org.asarkar.gradle.build-time-tracker") version buildTimeTrackerPluginVersion
    }
}

rootProject.name = "reactor-learning"

