val javaVersion: String by project
val tuprologVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api("it.unibo.alice.tuprolog", "2p-core", tuprologVersion)
    api(project(":linda-core"))
    implementation(project(":utils"))
    implementation(project(":prologx"))

    testImplementation("junit", "junit", junitVersion)
    testImplementation(project(":linda-test"))
    implementation(kotlin("stdlib-jdk8"))
}