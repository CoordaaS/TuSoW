import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    kotlin("jvm") version "1.3.50"
    application
}

val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val cliktVersion: String by project

dependencies {
    api(project(":jvm-client-logic"))
    api(project(":jvm-client-string"))

//    implementation(project(":utils"))
//    implementation(project(":prologx"))
//    implementation(kotlin("stdlib"))
//
//    api("io.vertx", "vertx-web-client", vertxVersion)

    // Use JUnit test framework
//    testImplementation("io.vertx", "vertx-unit", vertxVersion)
//    testImplementation("ch.qos.logback", "logback-classic", vertxVersion)
//    implementation("commons-cli", "commons-cli", commonsCliVersion)
//    implementation("info.picocli", "picocli", picocliVersion)
    implementation("com.github.ajalt", "clikt", cliktVersion)


    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

repositories {
    mavenCentral()
}

application {
    mainClassName = "it.unibo.coordination.linda.cli.TusowCommandKt"
}

