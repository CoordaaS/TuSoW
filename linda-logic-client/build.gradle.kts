val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-logic"))
    api(project(":linda-remote-client"))
    api(project(":linda-logic-presentation"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":prologx"))

    api("io.vertx", "vertx-web-client", vertxVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}