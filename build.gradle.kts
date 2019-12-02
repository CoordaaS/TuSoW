group = "it.unibo.coordination"
version = "0.1.1"

plugins {
    kotlin("jvm") version "1.3.50"
    `maven-publish`
    signing
    id("com.jfrog.bintray") version "1.8.4"
}

allprojects {
    // In this section you declare where to find the dependencies of all projects
    repositories {
        mavenCentral()
    }

    group = rootProject.group
    version = rootProject.version
}

fun capitalize(s: String): String {
    return s[0].toUpperCase() + s.substring(1)
}

fun getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

// env ORG_GRADLE_PROJECT_signingKey
val signingKey = getPropertyOrWarnForAbsence("signingKey")
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
// env ORG_GRADLE_PROJECT_bintrayUser
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
// env ORG_GRADLE_PROJECT_bintrayKey
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")

val publishAllToBintrayTask = tasks.create<DefaultTask>("publishAllToBintray") {
    group = "publishing"
}

subprojects {

    group = rootProject.group
    version = rootProject.version

    // ** NOTE ** legacy plugin application, because the new "plugins" block is not available inside "subprojects" scope yet
    // when it will be available it should be moved here
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "com.jfrog.bintray")

// https://central.sonatype.org/pages/requirements.html
    // https://docs.gradle.org/current/userguide/signing_plugin.html
    publishing {
        publications.withType<MavenPublication> {
            groupId = project.group.toString()
            version = project.version.toString()

//            val docArtifact = "packDokka${capitalize(name)}"
//
//            if (docArtifact in tasks.names) {
//                artifact(tasks.getByName(docArtifact)) {
//                    classifier = "javadoc"
//                }
//            }

            pom {
                name.set("Coordination -- Module `${this@subprojects.name}`")
                description.set("Tuple-based Coordination environment")
                url.set("https://gitlab.com/pika-lab/tuples/coordination")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        name.set("Giovanni Ciatto")
                        email.set("giovanni.ciatto@gmail.com")
                        url.set("https://about.me/gciatto")
                        organization.set("University of Bologna")
                        organizationUrl.set("https://www.unibo.it/it")
                    }
                }

                scm {
                    connection.set("scm:git:git:///gitlab.com/pika-lab/tuples/coordination.git")
                    url.set("https://gitlab.com/pika-lab/tuples/coordination")
                }
            }

        }

//        repositories {
//            val mavenRepoUrl = if (version.toString().contains("SNAPSHOT")) {
//                "https://oss.sonatype.org/content/repositories/snapshots/"
//            } else {
//                "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
//            }
//
//            maven(mavenRepoUrl) {
//                credentials {
//                    username = project.property("ossrhUsername").toString()
//                    password = project.property("ossrhPassword").toString()
//                }
//            }
//        }

        bintray {
            user = bintrayUser
            key = bintrayKey
//            setPublications(*publications.toList().toTypedArray())
            override = true
            with(pkg) {
                repo = "coordination"
                name = project.name
                userOrg = "pika-lab"
                vcsUrl = "https://gitlab.com/pika-lab/tuples/coordination"
                setLicenses("Apache-2.0")
                with(version) {
                    name = project.version.toString()
                }
            }
        }

        tasks.withType<com.jfrog.bintray.gradle.tasks.BintrayUploadTask> {
            publishAllToBintrayTask.dependsOn(this)
        }
    }

    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)

        sign(publishing.publications)

//        println("Configuring signing for the following publications: ${
//            publishing.publications.names.map { project.name + "-" + it }.joinToString(", ")
//        }")
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${capitalize(it.name)}Publication" }

        task<Sign>("signAllPublications") {
            dependsOn(*pubs.toTypedArray())
        }
    }
}