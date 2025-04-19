import co.early.n8.Shared

plugins {
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.androidAppPlugin)
    alias(libs.plugins.kotlinKaptPlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.composeCompilerPlugin)
}

val appId = "foo.bar.n8"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.toolchain.get().toInt()))
    }
}

android {

    namespace = appId

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        versionCode = 1
        versionName = "0.5"
        applicationId = appId
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            // keytool -genkey -v -keystore debug.fake_keystore -storetype PKCS12 -alias android -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 20000 -dname "cn=Unknown, ou=Unknown, o=Unknown, c=Unknown"
            storeFile = file("../keystore/debug.fake_keystore")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "../proguard-example-app.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    lint {
        abortOnError = true
        lintConfig = File(project.rootDir, "lint-example-app.xml")
    }
}

dependencies {

    implementation(project(":n8-core"))
    implementation(project(":n8-compose"))
    //implementation("co.early.n8:n8-core:${Shared.Publish.LIB_VERSION_NAME}")
    //implementation("co.early.n8:n8-compose:${Shared.Publish.LIB_VERSION_NAME}")

    // serialization
    implementation(libs.kotlinx.serialization)
    // reactivity
    implementation(libs.fore.core)
   // implementation(libs.fore.compose)
    // di
    implementation(libs.koin.compose)
    implementation(libs.koin.core)
    // build
    implementation(libs.multidex)
    // compose
    implementation(libs.compose.activity)
    implementation(libs.compose.constraint.layout)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    // test
    testImplementation(libs.kotlin.test)
}
