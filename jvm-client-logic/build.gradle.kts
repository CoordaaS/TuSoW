import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.3.31"
}

val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-presentation"))

    implementation(project(":utils"))
    implementation(project(":prologx"))
    implementation(kotlin("stdlib"))

    api("io.vertx", "vertx-web-client", vertxVersion)

    // Use JUnit test framework
//    testImplementation("io.vertx", "vertx-unit", vertxVersion)
//    testImplementation("ch.qos.logback", "logback-classic", vertxVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}