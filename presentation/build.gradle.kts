import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    implementation(project(":utils"))
    implementation(project(":prologx"))

    implementation(kotlin("stdlib-jdk8"))

    implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
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