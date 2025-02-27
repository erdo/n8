import co.early.n8.applyPublishingConfig
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.dokkaPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    id("maven-publish")
    id("signing")
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }

    androidTarget{
        publishLibraryVariants("release")
    }

    jvm()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                // serialization
                implementation(libs.kotlinx.serialization)
                // coroutines
                implementation(libs.kotlinx.coroutines.core)
                // persistence
                api(libs.persista)
                // reactivity
                api(libs.fore.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "co.early.n8.compose"

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-library.xml")
    }

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = false
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

ext.apply {
    set("LIB_ARTIFACT_ID", "n8-core")
    set("LIB_DESCRIPTION", "n8 pure kotlin persistent navigation backstack")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

applyPublishingConfig()