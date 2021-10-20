import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
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

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

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

    apply(rootProject.file("publish-on-maven.gradle.kts"))
}
