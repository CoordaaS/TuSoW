plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val junitVersion: String by project

dependencies {

    testImplementation("junit", "junit", junitVersion)
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
}
