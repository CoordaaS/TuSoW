import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("org.jetbrains.dokka")
}

val javaVersion: String by project
val ktFreeCompilerArgs: String by project

group = "it.unibo.coordaas"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    fullHash.set(false) // set to true if you want to use the full git hash
    maxVersionLength.set(Int.MAX_VALUE) // Useful to limit the maximum version length, e.g. Gradle Plugins have a limit on 20
    developmentCounterLength.set(2) // How many digits after `dev`
    enforceSemanticVersioning.set(true) // Whether the plugin should stop if the resulting version is not a valid SemVer, or just warn
    // The separator for the pre-release block.
    // Changing it to something else than "+" may result in non-SemVer compatible versions
    preReleaseSeparator.set("-")
    // The separator for the build metadata block.
    // Some systems (notably, the Gradle plugin portal) do not support versions with a "+" symbol.
    // In these cases, changing it to "-" is appropriate.
    buildMetadataSeparator.set("+")
    distanceCounterRadix.set(36) // The radix for the commit-distance counter. Must be in the 2-36 range.
    // A prefix on tags that should be ignored when computing the Semantic Version.
    // Many project are versioned with tags named "vX.Y.Z", de-facto building valid SemVer versions but for the leading "v".
    // If it is the case for some project, setting this property to "v" would make these tags readable as SemVer tags.
    versionPrefix.set("")
    assignGitSemanticVersion()
}

println("${rootProject.name}, version: $version")

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")

    java {
        targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
        sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
        withSourcesJar()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.$javaVersion"
            freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
        }
    }

    apply(rootProject.file("maven-publication.gradle.kts"))

    val dokkaHtml: DokkaTask by project.tasks
    val javadoc: Javadoc by project.tasks

    tasks.create<Jar>("dokkaHtmlJar") {
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        destinationDirectory.set(javadoc.destinationDir)
        dependsOn(dokkaHtml)
    }
}
