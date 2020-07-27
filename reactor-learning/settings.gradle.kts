pluginManagement {
    val kotlinPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinPluginVersion
    }
}

rootProject.name = "reactor-learning"

