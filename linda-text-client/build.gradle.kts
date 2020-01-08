val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-text"))
    api(project(":linda-remote-client"))
    api(project(":linda-text-presentation"))
    implementation(kotlin("stdlib-jdk8"))

    api("io.vertx", "vertx-web-client", vertxVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
}