import co.early.n8.applyPublishingConfig
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiPlatformPlugin)
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.composeCompilerPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.dokkaPlugin)
    id("maven-publish")
    id("signing")
}


kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }

    targets.withType<org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget> {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.target.get()))
                }
            }
        }
    }

    androidTarget{
        publishLibraryVariants("release")
    }

    applyDefaultHierarchyTemplate()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":n8-core"))
                api(libs.persista)
                api(libs.fore.core)
                api(libs.fore.compose)
                api(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.fore.test.fixtures)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.activity)
                implementation(libs.compose.ui)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.ui)
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
