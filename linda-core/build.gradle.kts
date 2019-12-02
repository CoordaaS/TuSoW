import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val tuprologVersion: String by project
val apacheCommonsVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project
val slf4jVersion: String by project

dependencies {
    api("org.apache.commons", "commons-collections4", apacheCommonsVersion)
    api("org.slf4j", "slf4j-api", slf4jVersion)
    api(project(":utils"))

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit", "junit", junitVersion)
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion
        freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
    }
}