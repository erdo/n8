import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerializationPlugin)
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


    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    val xcFramework = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true

            //until we use the XCFramework from fore itself?
            export(libs.fore.core)
            export(libs.fore.compose)
            export(libs.persista)
            export(libs.n8.core)
            export(libs.n8.compose)
            export(libs.okio)
            export(libs.kotlinx.serialization)

            xcFramework.add(this)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.fore.core)
            api(libs.fore.compose)
            api(libs.persista)
            api(libs.n8.core)
            api(libs.n8.compose)
            api(libs.kotlinx.serialization)
            api(libs.okio)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            api(libs.fore.test.fixtures)
        }
    }
}

android {
    namespace = "com.kmpfoo.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


//   Build the XCFramework and copy it over to the iosApp
//
//   ./gradlew :shared:prepAllXCFrameworkForXcode
//
//   then from XCode, add framework
//   you might need JDK specified explicitly in gradle.properties org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home

fun prepXCFrameworkForXcode(config: String) = tasks.registering(Sync::class) {
    from(layout.buildDirectory.dir("XCFrameworks/$config/shared.xcframework"))
    into(rootProject.layout.projectDirectory.dir("iosApp/Frameworks/shared.$config.xcframework"))
    dependsOn("assembleXCFramework")
}
val prepDebugXCFrameworkForXcode by prepXCFrameworkForXcode("debug")
val prepReleaseXCFrameworkForXcode by prepXCFrameworkForXcode("release")

tasks.register("prepAllXCFrameworkForXcode") {
    dependsOn(prepDebugXCFrameworkForXcode, prepReleaseXCFrameworkForXcode)
}
