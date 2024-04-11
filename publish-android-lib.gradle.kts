import co.early.n8.Shared
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.net.URI

apply(plugin = "maven-publish")
apply(plugin = "signing")

val LIB_ARTIFACT_ID: String? by project
val LIB_DESCRIPTION: String? by project

println("[$LIB_ARTIFACT_ID android lib publish file]")

group = "${Shared.Publish.LIB_GROUP}"
version = "${Shared.Publish.LIB_VERSION_NAME}"

project.afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {

                from(components["release"])

                groupId = "${Shared.Publish.LIB_GROUP}"
                artifactId = LIB_ARTIFACT_ID
                version = "${Shared.Publish.LIB_VERSION_NAME}"

                pom {
                    name.set("${Shared.Publish.PROJ_NAME}")
                    description.set(LIB_DESCRIPTION)
                    url.set("${Shared.Publish.POM_URL}")

                    licenses {
                        license {
                            name.set("${Shared.Publish.LICENCE_NAME}")
                            url.set("${Shared.Publish.LICENCE_URL}")
                        }
                    }
                    developers {
                        developer {
                            id.set("${Shared.Publish.LIB_DEVELOPER_ID}")
                            name.set("${Shared.Publish.LIB_DEVELOPER_NAME}")
                            email.set("${Shared.Publish.LIB_DEVELOPER_EMAIL}")
                        }
                    }
                    scm {
                        connection.set("${Shared.Publish.POM_SCM_CONNECTION}")
                        developerConnection.set("${Shared.Publish.POM_SCM_CONNECTION}")
                        url.set("${Shared.Publish.POM_SCM_URL}")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "mavenCentral"

                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                val repoUrl = if (Shared.Publish.LIB_VERSION_NAME.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                url = URI(repoUrl)

                credentials {
                    username = "${Shared.Secrets.MAVEN_USER}"
                    password = "${Shared.Secrets.MAVEN_PASSWORD}"
                }
            }
        }
    }

    configure<SigningExtension> {

        extra["signing.keyId"] = "${Shared.Secrets.SIGNING_KEY_ID}"
        extra["signing.password"] = "${Shared.Secrets.SIGNING_PASSWORD}"
        extra["signing.secretKeyRingFile"] = "${Shared.Secrets.SIGNING_KEY_RING_FILE}"

        val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
        val publication = pubExt.publications["release"]
        sign(publication)
    }
}
