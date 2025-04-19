import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// https://youtrack.jetbrains.com/issue/KTIJ-24981
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

include(":example-android-app")
include(":n8-core")
include(":n8-compose")
rootProject.name = "n8"
