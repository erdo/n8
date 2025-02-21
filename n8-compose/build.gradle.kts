plugins {
    id("kotlin-android")
    id("com.android.library")
    alias(libs.plugins.compose)
}

ext.apply {
    set("LIB_ARTIFACT_ID", "n8-compose")
    set("LIB_DESCRIPTION", "n8 pure kotlin persistent navigation backstack")
}

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmToolchain.get().toInt()))
    }
}

val appId = "co.early.n8.compose"

android {
    namespace = appId

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("../proguard-library-consumer.pro")
        }
    }

    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "app/lint-library.xml")
    }

    buildFeatures {
        buildConfig = false
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(project(":n8-core"))
    implementation(libs.fore.compose)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
}

apply(from = "../publish-android-lib.gradle.kts")
