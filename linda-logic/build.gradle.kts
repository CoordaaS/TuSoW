plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

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
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}
