apply(plugin = "maven-publish")
apply(plugin = "signing")

private val alreadyWarned = mutableSetOf<String>()

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank() && key !in alreadyWarned) {
        System.err.println("WARNING: $key is not set")
        alreadyWarned += key
    }
    return value
}

// env ORG_GRADLE_PROJECT_signingKey
val signingKey = getPropertyOrWarnForAbsence("signingKey")
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")

project.configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        groupId = project.group.toString()
        version = project.version.toString()

        setArtifacts(tasks.withType<Jar>())

        pom {
            name.set("Coordination -- Module `${project.name}`")
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

    configure<SigningExtension> {
        if (arrayOf(signingKey, signingPassword).none { it.isNullOrBlank() }) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publications)
        }

        val signAll = project.tasks.create("signAllPublications")
        project.tasks.withType<Sign> {
            signAll.dependsOn(this)
        }
    }
}
