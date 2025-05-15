/**
 * ./gradlew check
 *
 * ./gradlew test
 * ./gradlew testDebugUnitTest
 * ./gradlew connectedAndroidTest
 *
 * ./gradlew ktlintFormat
 * ./gradlew ktlintCheck
 *
 * ./gradlew clean
 * ./gradlew publishToMavenLocal
 * ./gradlew publishAllPublicationsToMavenCentralRepository --no-daemon --no-parallel
 *
 * ./gradlew :buildEnvironment
 *
 * ./gradlew :n8-core:dependencies
 *
 * git tag -a v1.5.9 -m 'v1.5.9'
 * git push origin --tags
 */
plugins {
    alias(libs.plugins.androidAppPlugin) apply false
    alias(libs.plugins.androidLibraryPlugin) apply false
    alias(libs.plugins.kotlinAndroidPlugin) apply false
    alias(libs.plugins.kotlinJvmPlugin) apply false
    alias(libs.plugins.kotlinKaptPlugin) apply false
    alias(libs.plugins.kotlinMultiPlatformPlugin) apply false
    alias(libs.plugins.kotlinSerializationPlugin) apply false
    alias(libs.plugins.composeCompilerPlugin) apply false
    alias(libs.plugins.kotlinCocoapodsPlugin) apply false
}
