plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val javaTuplesVersion: String by project
val junitVersion: String by project

dependencies {

    api("junit", "junit", junitVersion)
    api(project(":linda-core"))
    api(project(":test-utils"))
    api("org.javatuples", "javatuples", javaTuplesVersion)
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}
