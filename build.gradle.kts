import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
    `maven-publish`
    signing
    id ("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("com.github.johnrengelman.shadow") version Versions.com_github_johnrengelman_shadow_gradle_plugin apply false
}

val javaVersion: String by project
val ktFreeCompilerArgs: String by project

group = "it.unibo.coordaas"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    developmentCounterLength.set(2) // How many digits after `dev`
    version = computeGitSemVer() // THIS IS MANDATORY, AND MUST BE LAST IN THIS BLOCK!
}

println("${rootProject.name}, version: $version")

allprojects {
    repositories {
        mavenCentral()
    }

    group = rootProject.group
    version = rootProject.version
}

// env ORG_GRADLE_PROJECT_signingKey
val signingKey = getPropertyOrWarnForAbsence("signingKey")
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    java {
        targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
        sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
//        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.$javaVersion"
            freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            groupId = project.group.toString()
            version = project.version.toString()

            setArtifacts(tasks.withType<Jar>())

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
    }

    signing {
        if (arrayOf(signingKey, signingPassword).none { it.isNullOrBlank() }) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }

    publishing {
        val signAll = project.tasks.create("signAllPublications")
        project.tasks.withType<Sign> {
            signAll.dependsOn(this)
        }
    }
}
