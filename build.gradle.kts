
plugins {
    kotlin("jvm") version "1.3.60"
    `maven-publish`
    signing
    id("com.jfrog.bintray") version "1.8.4"
    id ("org.danilopianini.git-sensitive-semantic-versioning") version "0.2.2"
}

val javaVersion: String by project
val ktFreeCompilerArgs: String by project

group = "it.unibo.coordination"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    developmentCounterLength.set(2) // How many digits after `dev`
    version = computeGitSemVer() // THIS IS MANDATORY, AND MUST BE LAST IN THIS BLOCK!
}

println("Coordination, version: $version")

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

    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "com.jfrog.bintray")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<JavaPluginConvention> {
        targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
        sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
//            jvmTarget = "1.$javaVersion"
            jvmTarget = javaVersion
            freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
        }
    }

    // https://central.sonatype.org/pages/requirements.html
    // https://docs.gradle.org/current/userguide/signing_plugin.html
    publishing {

        publications.create<MavenPublication>("maven") {
            groupId = project.group.toString()
            version = project.version.toString()

            from(components["java"])

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

        bintray {
            user = bintrayUser
            key = bintrayKey
            setPublications("maven")
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
//        useGpgCmd()
        sign(publishing.publications)
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${capitalize(it.name)}Publication" }

        task<Sign>("signAllPublications") {
            dependsOn(*pubs.toTypedArray())
        }
    }
}