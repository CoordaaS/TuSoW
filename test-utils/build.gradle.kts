val javaVersion: String by project
val junitVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    implementation("junit", "junit", junitVersion)
    implementation(kotlin("stdlib-jdk8"))
}