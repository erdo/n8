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

import co.early.n8.Shared
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.net.URI

apply(plugin = "maven-publish")
apply(plugin = "signing")

val LIB_ARTIFACT_ID: String? by project
val LIB_DESCRIPTION: String? by project

println("[$LIB_ARTIFACT_ID lib publish file]")

group = "${co.early.n8.Shared.Publish.LIB_GROUP}"
version =  "${co.early.n8.Shared.Publish.LIB_VERSION_NAME}"

configure<PublishingExtension> {
	publications {
		create<MavenPublication>("release") {

			groupId = "${co.early.n8.Shared.Publish.LIB_GROUP}"
			artifactId = LIB_ARTIFACT_ID
			version = "${co.early.n8.Shared.Publish.LIB_VERSION_NAME}"

			val binaryJar = components["java"]

			val sourcesJar by tasks.creating(Jar::class) {
				archiveClassifier.set("sources")
				from(project.the<SourceSetContainer>()["main"].allSource)
			}

			val javadocJar: Jar by tasks.creating(Jar::class) {
				archiveClassifier.set("javadoc")
				from("$buildDir/javadoc")
			}

			from(binaryJar)
			artifact(sourcesJar)
			artifact(javadocJar)

			pom {
				name.set("${co.early.n8.Shared.Publish.PROJ_NAME}")
				description.set(LIB_DESCRIPTION)
				url.set("${co.early.n8.Shared.Publish.POM_URL}")

				licenses {
					license {
						name.set("${co.early.n8.Shared.Publish.LICENCE_NAME}")
						url.set("${co.early.n8.Shared.Publish.LICENCE_URL}")
					}
				}
				developers {
					developer {
						id.set("${co.early.n8.Shared.Publish.LIB_DEVELOPER_ID}")
						name.set("${co.early.n8.Shared.Publish.LIB_DEVELOPER_NAME}")
						email.set("${co.early.n8.Shared.Publish.LIB_DEVELOPER_EMAIL}")
					}
				}
				scm {
					connection.set("${co.early.n8.Shared.Publish.POM_SCM_CONNECTION}")
					developerConnection.set("${co.early.n8.Shared.Publish.POM_SCM_CONNECTION}")
					url.set("${co.early.n8.Shared.Publish.POM_SCM_URL}")
				}
			}
		}
	}
	repositories {
		maven {
			name = "mavenCentral"

			val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
			val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
			val repoUrl = if (co.early.n8.Shared.Publish.LIB_VERSION_NAME.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

			url = URI(repoUrl)

			credentials {
				username = "${co.early.n8.Shared.Secrets.MAVEN_USER}"
				password = "${co.early.n8.Shared.Secrets.MAVEN_PASSWORD}"
			}
		}
	}
}

configure<SigningExtension> {

	extra["signing.keyId"] = "${co.early.n8.Shared.Secrets.SIGNING_KEY_ID}"
	extra["signing.password"] = "${co.early.n8.Shared.Secrets.SIGNING_PASSWORD}"
	extra["signing.secretKeyRingFile"] = "${co.early.n8.Shared.Secrets.SIGNING_KEY_RING_FILE}"

	val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
	val publication = pubExt.publications["release"]
	sign(publication)
}

