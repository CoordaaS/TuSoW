val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", junitVersion)
}