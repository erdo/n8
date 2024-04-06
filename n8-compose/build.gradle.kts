import co.early.n8.Shared

plugins {
    id("kotlin-android")
    id("com.android.library")
}

ext.apply {
    set("LIB_ARTIFACT_ID", "n8-compose")
    set("LIB_DESCRIPTION", "n8 pure kotlin persistent navigation backstack")
}
//val LIB_ARTIFACT_ID by extra("n8-compose")
//val LIB_DESCRIPTION by extra("n8 pure kotlin persistent navigation backstack")

println("[${ext.get("LIB_ARTIFACT_ID")} build file]")
//println("[$LIB_ARTIFACT_ID build file]")

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

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get()
    }

    // register (not create) - we want this to run after the rest of the android
    // block has been configured in the individual build files as they add files
    // to the source sets
    project.tasks.register("androidSourcesJar", Jar::class.java) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").java.srcDirs)
    }
}

dependencies {
    api(project(":n8-core"))
    implementation(libs.fore.compose)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation("androidx.compose.ui:ui")
}

apply(from = "../publish-android-lib.gradle.kts")
