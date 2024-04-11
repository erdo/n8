/**
 * ./gradlew check
 *
 * ./gradlew test
 * ./gradlew testDebugUnitTest
 * ./gradlew connectedAndroidTest
 *
 * ./gradlew clean
 * ./gradlew publishToMavenLocal
 * ./gradlew publishReleasePublicationToMavenCentralRepository --no-daemon --no-parallel
 *
 * ./gradlew :buildEnvironment
 *
 * ./gradlew :persista-lib:dependencies
 *
 * git tag -a v1.5.9 -m 'v1.5.9'
 * git push origin --tags
 */

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.kotlinSerializationPlugin)
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.layout.buildDirectory)
}
