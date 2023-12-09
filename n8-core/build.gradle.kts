plugins {
    id("kotlin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val LIB_ARTIFACT_ID by extra("n8")
val LIB_DESCRIPTION by extra("n8 pure kotlin persistent navigation backstack")

println("[$LIB_ARTIFACT_ID build file]")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    // persistence
    implementation(libs.persista)
    // serialization
    implementation(libs.kotlinx.serialization)
    // network
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.serialization)
    implementation(libs.fore.network)
    // gql
    implementation(libs.apollo)
    // db
    implementation(libs.sqldelight.core)
}


dependencies {
    // kotlin
    implementation(libs.kotlin.stdlib)
    // persistence
    api(libs.persista)
    // serialization
    implementation(libs.kotlinx.serialization)
    // reactivity
    api(libs.fore.core)

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.11.0")
}

apply(from = "../publish-lib.gradle.kts")
