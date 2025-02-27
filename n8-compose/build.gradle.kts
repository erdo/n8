import co.early.n8.applyPublishingConfig
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.dokkaPlugin)
    alias(libs.plugins.composePlugin)
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
                api(project(":n8-core"))
                api(libs.persista)
                api(libs.fore.core)
              //  implementation(libs.fore.compose)
                api(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.serialization)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.compose.bom))
                implementation(libs.compose.activity)
                implementation(libs.compose.ui)
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
    set("LIB_ARTIFACT_ID", "n8-compose")
    set("LIB_DESCRIPTION", "n8 pure kotlin persistent navigation backstack")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

applyPublishingConfig()
