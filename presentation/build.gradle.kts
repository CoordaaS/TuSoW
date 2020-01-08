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

    api("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    api("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
}