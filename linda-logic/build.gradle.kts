plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val javaVersion: String by project
val tuprologVersion: String by project
val junitVersion: String by project

dependencies {
    api("it.unibo.alice.tuprolog", "tuprolog", tuprologVersion)
    api(project(":linda-core"))
    implementation(project(":utils"))
    implementation(project(":prologx"))

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":linda-test"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
}
