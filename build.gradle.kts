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
