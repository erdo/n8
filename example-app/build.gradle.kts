plugins {
    id("kotlin-kapt")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.application")
}

val appId = "foo.bar.n8"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

android {

    namespace = appId

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get()
    }

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        versionCode = 1
        versionName = "0.5"
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
    //implementation("co.early.n8:n8:1.0.0")

    // serialization
    implementation(libs.kotlinx.serialization)
    // reactivity
    implementation(libs.fore.android)
    implementation(libs.fore.compose)
    // di
    implementation(libs.koin.compose)
    implementation(libs.koin.core)
    // build
    implementation(libs.multidex)
    // compose
    implementation(libs.compose.activity)
    implementation(libs.compose.constraint.layout)
    implementation(platform(libs.compose.bom))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.11.0")
}
