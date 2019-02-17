plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

val tuprologVersion: String by project
val apacheCommonsVersion: String by project
val junitVersion: String by project

dependencies {
    api("it.unibo.alice.tuprolog", "tuprolog", tuprologVersion)
    api("org.apache.commons", "commons-collections4", apacheCommonsVersion)

    testImplementation("junit", "junit", junitVersion)
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.VERSION_1_10
    sourceCompatibility = JavaVersion.VERSION_1_10
}
