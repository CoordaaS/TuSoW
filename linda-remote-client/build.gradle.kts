import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-core"))
    api(project(":linda-presentation"))
    implementation(kotlin("stdlib-jdk8"))

    api("io.vertx", "vertx-web-client", vertxVersion)

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