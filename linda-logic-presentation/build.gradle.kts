val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-logic"))
    api(project(":presentation"))
    implementation(project(":prologx"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":test-utils"))
    testImplementation(project(":linda-test"))
}