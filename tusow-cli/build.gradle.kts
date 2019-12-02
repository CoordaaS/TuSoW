plugins {
    application
}

val javaVersion: String by project
val joolVersion: String by project
val junitVersion: String by project
val jacksonVersion: String by project
val vertxVersion: String by project
val commonsCliVersion: String by project
val cliktVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api(project(":linda-logic-client"))
    api(project(":linda-text-client"))

    implementation("com.github.ajalt", "clikt", cliktVersion)

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":tusow"))
    testImplementation(project(":linda-test"))
    testImplementation(project(":test-utils"))
    implementation(kotlin("stdlib-jdk8"))
}

application {
    mainClassName = "it.unibo.coordination.linda.cli.TusowCommandKt"
}

