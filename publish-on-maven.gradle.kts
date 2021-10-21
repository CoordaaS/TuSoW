apply(plugin = "maven-publish")
apply(plugin = "signing")

// env ORG_GRADLE_PROJECT_signingKey
val signingKey: String? by project
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword: String? by project
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername: String? by project
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword: String? by project

project.configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        groupId = project.group.toString()
        version = project.version.toString()

        setArtifacts(tasks.withType<Jar>())

        pom {
            name.set("Coordination -- Module `${project.name}`")
            description.set("Tuple-based Coordination environment")
            url.set("https://github.com/CoordaaS/TuSoW")
            licenses {
                license {
                    name.set("Apache 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    name.set("Giovanni Ciatto")
                    email.set("giovanni.ciatto@unibo.it")
                    url.set("https://about.me/gciatto")
                    organization.set("University of Bologna")
                    organizationUrl.set("https://www.unibo.it/it")
                }
            }

            scm {
                connection.set("scm:git:git:///github.com/CoordaaS/TuSoW.git")
                url.set("https://github.com/CoordaaS/TuSoW")
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
