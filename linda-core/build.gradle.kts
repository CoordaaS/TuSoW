val javaVersion: String by project
val tuprologVersion: String by project
val apacheCommonsVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project
val slf4jVersion: String by project

dependencies {
    api("org.apache.commons", "commons-collections4", apacheCommonsVersion)
    api("org.slf4j", "slf4j-api", slf4jVersion)
    api(project(":utils"))

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit", "junit", junitVersion)
}
