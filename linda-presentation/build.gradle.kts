plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val namedRegexpVersion: String by project
val junitVersion: String by project

dependencies {
    api(project(":linda-core"))
    api(project(":linda-logic"))
    api(project(":linda-objects"))

    implementation(project(":utils"))
    implementation(project(":prologx"))

    implementation("com.fasterxml.jackson.core:jackson-core:2.9.8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")
    implementation("org.jooq:jool:0.9.14")

    testImplementation("junit", "junit", "4.12")
    testImplementation(project(":test-utils"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
}