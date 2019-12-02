val javaVersion: String by project
val tuprologVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    api("it.unibo.alice.tuprolog", "2p-presentation", tuprologVersion)

    testImplementation("junit", "junit", junitVersion)
    implementation(kotlin("stdlib-jdk8"))
}