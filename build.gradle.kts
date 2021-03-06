import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

buildscript {
    repositories {
        mavenCentral()
//        jcenter()
        gradlePluginPortal()
    }
}


plugins {
    kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
    `maven-publish`
    signing
    id("com.jfrog.bintray") version Versions.com_jfrog_bintray_gradle_plugin
    id ("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
    id("com.github.breadmoirai.github-release") version Versions.com_github_breadmoirai_github_release_gradle_plugin
    id("com.github.johnrengelman.shadow") version Versions.com_github_johnrengelman_shadow_gradle_plugin apply false
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
// env ORG_GRADLE_PROJECT_bintrayUser
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
// env ORG_GRADLE_PROJECT_bintrayKey
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")
// env ORG_GRADLE_PROJECT_gitHubToken
val gitHubToken = getPropertyOrWarnForAbsence("gitHubToken")

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
    apply(plugin = "com.github.johnrengelman.shadow")

    configure<JavaPluginConvention> {
        targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
        sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.$javaVersion"
            freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
        }
    }

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

        tasks.withType<BintrayUploadTask> {
            publishAllToBintrayTask.dependsOn(this)
        }
    }

    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${it.name.capitalize()}Publication" }

        task<Sign>("signAllPublications") {
            dependsOn(*pubs.toTypedArray())
        }
    }
}

if (gitHubToken?.isNotBlank() ?: false) {

    val jarTasks: List<Jar> = subprojects("tusow-service", "tusow-cli", "tusow-full")
            .flatMap { it.tasks.withType(Jar::class) }
            .filter { it.name == "shadowJar" }
            .toList()

    configure<GithubReleaseExtension> {
        token(gitHubToken)
        owner("tuple-based-coord")
        repo("TuSoW")
        tagName { version.toString() }
        releaseName { version.toString() }
        overwrite { true }
        allowUploadToExisting { true }
        prerelease { !isFullVersion }
        draft { false }
        releaseAssets(*jarTasks.map { it.archiveFile }.toTypedArray())
    }

    fun setUpChangelog() {
        configure<GithubReleaseExtension> {
            body("""|
                |## CHANGELOG
                |${changelog().call()}
                """.trimMargin())
        }
    }

    tasks.withType(GithubReleaseTask::class) {
        dependsOn(*jarTasks.toTypedArray())
        doFirst {
            setUpChangelog()
        }
    }
}
