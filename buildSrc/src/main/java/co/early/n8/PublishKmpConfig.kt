package co.early.n8

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

fun Project.applyPublishingConfig() {

    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    val LIB_ARTIFACT_ID: String? by project
    val LIB_DESCRIPTION: String? by project

    println("[$LIB_ARTIFACT_ID lib publish file]")

    group = Shared.Publish.LIB_GROUP
    version = Shared.Publish.LIB_VERSION_NAME

    afterEvaluate {// otherwise android won't have finished creating the release publication

        configure<PublishingExtension> {

            publications {

                fun MavenPublication.configurePom() {
                    pom {
                        name.set(Shared.Publish.PROJ_NAME)
                        description.set(LIB_DESCRIPTION)
                        url.set(Shared.Publish.POM_URL)

                        licenses {
                            license {
                                name.set(Shared.Publish.LICENCE_NAME)
                                url.set(Shared.Publish.LICENCE_URL)
                            }
                        }
                        developers {
                            developer {
                                id.set(Shared.Publish.LIB_DEVELOPER_ID)
                                name.set(Shared.Publish.LIB_DEVELOPER_NAME)
                                email.set(Shared.Publish.LIB_DEVELOPER_EMAIL)
                            }
                        }
                        scm {
                            connection.set(Shared.Publish.POM_SCM_CONNECTION)
                            developerConnection.set(Shared.Publish.POM_SCM_CONNECTION)
                            url.set(Shared.Publish.POM_SCM_URL)
                        }
                    }
                }

                // mavenCentral insists on javadocs for the jvm artifact
                findByName("jvm")?.let {
                    (it as MavenPublication).apply {
                        artifact(tasks.named("javadocJar"))
                    }
                }

                publications.forEach { publication ->
//                    println("publication: ${publication.name}")
                    (publication as MavenPublication).configurePom()
                }
            }

            repositories {
                maven {
                    name = "mavenCentral"

                    val releasesRepoUrl =
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl =
                        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    val repoUrl =
                        if (Shared.Publish.LIB_VERSION_NAME.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                    url = URI(repoUrl)

                    credentials {
                        username = Shared.Secrets.MAVEN_USER
                        password = Shared.Secrets.MAVEN_PASSWORD
                    }
                }
            }
        }
    }

    afterEvaluate {
        configure<SigningExtension> {
//        useInMemoryPgpKeys(
//            Shared.Secrets.SIGNING_KEY_ID,
//            Shared.Secrets.SIGNING_PRIVATE_KEY,
//            Shared.Secrets.SIGNING_PASSWORD
//        )
            extra["signing.keyId"] = Shared.Secrets.SIGNING_KEY_ID
            extra["signing.password"] = Shared.Secrets.SIGNING_PASSWORD
            extra["signing.secretKeyRingFile"] = Shared.Secrets.SIGNING_KEY_RING_FILE

            extensions.findByType<PublishingExtension>()?.publications?.forEach { publication ->
//                println("signing: ${publication.name}")
                sign(publication)
            }
        }
    }
}
