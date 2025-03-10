plugins {
    alias(libs.plugins.kotlinJvmPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
}

val LIB_ARTIFACT_ID by extra("n8-core")
val LIB_DESCRIPTION by extra("n8 pure kotlin persistent navigation backstack")

println("[$LIB_ARTIFACT_ID build file]")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    // serialization
    implementation(libs.kotlinx.serialization)
    // persistence
    api(libs.persista)
    // reactivity
    api(libs.fore.core)

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.11.0")
}

apply(from = "../publish-lib.gradle.kts")
