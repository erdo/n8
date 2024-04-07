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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.layout.buildDirectory)
}
