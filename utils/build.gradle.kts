val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project
val apacheCommonsVersion: String by project

dependencies {
    api("org.apache.commons", "commons-collections4", apacheCommonsVersion)
    api(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", junitVersion)
}