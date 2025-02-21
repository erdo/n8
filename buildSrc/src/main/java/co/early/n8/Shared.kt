package co.early.n8

import java.io.File
import java.lang.System.*
import java.util.*

object Shared {

    object Publish {

        const val LIB_VERSION_NAME = "1.0.0" //"x.x.x-SNAPSHOT"

        const val LIB_GROUP = "co.early.n8"
        const val PROJ_NAME = "n8"
        const val LIB_DEVELOPER_ID = "erdo"
        const val LIB_DEVELOPER_NAME = "E Donovan"
        const val LIB_DEVELOPER_EMAIL = "eric@early.co"
        const val POM_URL = "https://github.com/erdo/n8/"
        const val POM_SCM_URL = "https://github.com/erdo/n8/"
        const val POM_SCM_CONNECTION = "scm:git@github.com:erdo/n8.git"
        const val LICENCE_NAME = "The Apache Software License, Version 2.0"
        const val LICENCE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    }

    object Secrets {

        private val secrets = readProperties(File("../secrets/secrets.properties"))

        val MAVEN_USER = (getenv("MAVEN_USER") ?: secrets.getProperty("MAVEN_USER")) ?: "MISSING"
        val MAVEN_PASSWORD = (getenv("MAVEN_PASSWORD") ?: secrets.getProperty("MAVEN_PASSWORD")) ?: "MISSING"
        val SONATYPE_STAGING_PROFILE_ID = (getenv("SONATYPE_STAGING_PROFILE_ID") ?: secrets.getProperty("SONATYPE_STAGING_PROFILE_ID")) ?: "MISSING"
        val SIGNING_KEY_ID = (getenv("SIGNING_KEY_ID") ?: secrets.getProperty("SIGNING_KEY_ID")) ?: "MISSING"
        val SIGNING_PASSWORD = (getenv("SIGNING_PASSWORD") ?: secrets.getProperty("SIGNING_PASSWORD")) ?: "MISSING"
        val SIGNING_KEY_RING_FILE = (getenv("SIGNING_KEY_RING_FILE") ?: secrets.getProperty("SIGNING_KEY_RING_FILE")) ?: "MISSING"
    }
}

fun readProperties(propertiesFile: File): Properties {
    return Properties().apply {
        try {
            propertiesFile.inputStream().use { fis ->
                load(fis)
            }
            println("[SECRETS LOADED]\n")
        } catch (exception: Exception) {
            println("WARNING $propertiesFile not found! \n")
            println("exception: $exception \n")
        }
    }
}
